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
  activeTab = 'tab1';
  supportList: any;
  dontTouched:any[] = null;
  sortHelper = new SortHelper();
  tableSearch = new FormControl();
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
  }
  getSupportList(){
    this.supportService.getSupportList().then(resp=>{
      this.supportList = resp.data;
      this.dontTouched = [...resp.data];
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
      return this.supportList
    } else {
      let sorted = this.dontTouched.sort(
        (a, b) => {
          let aField = a[this.sortHelper.sort.sortBy];
          let bField = b[this.sortHelper.sort.sortBy];
          let res = aField == bField ? 0 : (aField > bField ? 1 : -1);
          return this.sortHelper.sort.increase ? res : -res;
        }
      );
      sorted = this.searchInputService.filterData(sorted, '');
      this.supportList = [...sorted];
      return this.supportList
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
