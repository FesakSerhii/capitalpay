import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {SupportService} from "../../service/support.service";
import {SortHelper} from '../../../../../../src/app/helper/sort-helper';
import {SearchInputService} from '../../../../../../src/app/service/search-input.service';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-help',
  templateUrl: './help.component.html',
  styleUrls: ['./help.component.scss']
})
export class HelpComponent implements OnInit {
  themes=[
    {title:'Изменение регистрационных данных',value:'Изменение регистрационных данных'},
    {title:'Общие проблемы с работой сервиса',value:'Общие проблемы с работой сервиса'},
    {title:'Проблемы с оплатой',value:'Проблемы с оплатой'},
    {title:'Прочее',value:'Прочее'},
    {title:'Все темы обращений',value:'Все темы обращений'}
  ]
  activeTab = 'tab1';
  supportList: any;
  supportListClosed: any;
  dontTouched:any[] = null;
  dontTouchedClosed:any[] = null;
  sortHelper = new SortHelper();
  tableSearch = new FormControl();
  themeForm = new FormControl();
  constructor( private router: Router, private supportService: SupportService, private searchInputService: SearchInputService) { }

  ngOnInit(): void {
    this.getSupportList()
    this.tableSearch.valueChanges.subscribe(val=>{
      if(val.length>3){
        this.supportList = this.searchInputService.filterData(this.sortedActions,val)
      }else{
        this.supportList = [...this.dontTouched];
      }
    })
    this.themeForm.valueChanges.subscribe(val=>{
      this.nextSort(null)
    })
  }
  getSupportList(){
    this.supportList = null;
    this.supportListClosed = null;
    this.dontTouched = null;
    this.dontTouchedClosed = null;
    this.supportService.getSupportList().then(resp=>{
      this.supportList = resp.data.filter(el=>el.status!=='closed').sort((a,b)=>b.timestamp-a.timestamp);
      this.supportListClosed = resp.data.filter(el=>el.status==='closed').sort((a,b)=>b.timestamp-a.timestamp);
      this.dontTouched = [...resp.data.filter(el=>el.status!=='closed').sort((a,b)=>b.timestamp-a.timestamp)];
      this.dontTouchedClosed = [...resp.data.filter(el=>el.status==='closed').sort((a,b)=>b.timestamp-a.timestamp)];
    })
  }
  navigateToSettings(id){
    this.router.navigate(['/admin-panel/help/chat'],{queryParams: {
      id: id,
    },
      queryParamsHandling: "merge"})
  }
  nextSort(field) {
    let sh: SortHelper = this.sortHelper;
    this.sortHelper = sh.nextSort(field);
  }
  get sortedActions() {
    if (this.sortHelper.sort.sortBy === null) {
      this.supportList = this.searchInputService.filterData(this.dontTouched, this.tableSearch.value);
      this.supportListClosed = this.searchInputService.filterData(this.dontTouchedClosed, this.tableSearch.value);
      if(this.themeForm.value!==null&&this.themeForm.value!=='Все темы обращений'){
        this.supportList = this.supportList.filter(item=>item.theme===this.themeForm.value);
        this.supportListClosed = this.supportListClosed.filter(item=>item.theme===this.themeForm.value);
      }
      return this.activeTab==='tab1'?this.supportList:this.supportListClosed
    } else {
      let sorted = this.dontTouched.sort(
        (a, b) => {
          let aField = a[this.sortHelper.sort.sortBy];
          let bField = b[this.sortHelper.sort.sortBy];
          let res = aField == bField ? 0 : (aField > bField ? 1 : -1);
          return this.sortHelper.sort.increase ? res : -res;
        }
      );
      let sortedClosed = this.dontTouchedClosed.sort(
        (a, b) => {
          let aField = a[this.sortHelper.sort.sortBy];
          let bField = b[this.sortHelper.sort.sortBy];
          let res = aField == bField ? 0 : (aField > bField ? 1 : -1);
          return this.sortHelper.sort.increase ? res : -res;
        }
      );
      sorted = this.searchInputService.filterData(sorted, '');
      sortedClosed = this.searchInputService.filterData(sortedClosed, '');
      this.supportList = [...sorted];
      this.supportListClosed = [...sortedClosed];
      return this.activeTab==='tab1'?this.supportList:this.supportListClosed
    }
  }

  setImportant(importance,item) {
    let data = {
      "requestId":item.id,
      "status":1,
      "important":importance
    }
    this.supportService.setImportant(data).then(resp=>{
      this.getSupportList();
    })
  }

}
