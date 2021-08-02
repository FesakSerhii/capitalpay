import { Component, OnInit } from '@angular/core';
import {UserService} from "../../projects/admin-panel/src/app/service/user.service";
import {Router} from "@angular/router";
import {PaymentsService} from '../../projects/admin-panel/src/app/service/payments.service';
import {SortHelper} from '../../src/app/helper/sort-helper';
import {SearchInputService} from '../../src/app/service/search-input.service';
import {FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-transactions-log',
  templateUrl: './transactions-log.component.html',
  styleUrls: ['./transactions-log.component.scss']
})
export class TransactionsLogComponent implements OnInit {

  constructor(private userService:UserService,private router:Router, private paymentsService:PaymentsService,private searchInputService: SearchInputService) { }
  activeTab = 'tab1';
  user:any;
  isAdmin:boolean;
  transactionList:any[] = null;
  dontTouched:any[] = null;
  transactionDetails:any = null;
  currentPage:number = 1;
  visible:boolean = true;
  selectedTransaction:any = null;
  sortHelper = new SortHelper();
  filter = new FormGroup({
    billId: new FormControl(),
    dateStart: new FormControl(),
    dateEnd: new FormControl(),
    merchantName: new FormControl(),
    paySysPayId: new FormControl(),
    totalAmount: new FormControl(),
    currency: new FormControl(),
    cashboxName: new FormControl(),
  })
  isFilterActive={
    billId: false,
    dateStart: false,
    dateEnd: false,
    merchantName: false,
    paySysPayId: false,
    totalAmount: false,
    currency: false,
    cashboxName: false,
  }
  currencyList=[];
  tableSearch = new FormControl();

  ngOnInit(): void {
    this.user = this.userService.getUserInfo();
    this.isAdmin = this.router.url.includes('admin-panel');
    this.getTransactions();
    this.tableSearch.valueChanges.subscribe(val=>{
      if(val.length>3){
        this.transactionList = this.searchInputService.filterData(this.sortedActions,val)
      }else{
        this.transactionList = [...this.dontTouched];
      }
    })

  }
  async getTransactions(){
    this.transactionList = {...await this.paymentsService.getTransactionsList()}.data
    this.dontTouched = [...this.transactionList]
   for (const item of this.transactionList){
     if(this.currencyList.indexOf(item.currency)===-1){
       this.currencyList.push(item.currency)
     }
   }
  }
  formRegistre() {
    this.router.navigate([(this.isAdmin?'admin-panel':'/merchant')+'/transaction-log/registry'])
  }

  changePage(event) {
    this.visible = false;
    this.currentPage = event;
    this.visible = true;
  }

  async getTransactionDetails(id) {
    const obj = {...await this.paymentsService.getTransactionDetails(id)}.data
    const arr = [];
    for(const field in obj){
      arr.push({field:field, value:obj[field]})
    }
    this.transactionDetails = [...arr]
  }
  getGuid(data){
   return data.filter(el=>el.field==='guid')[0].value
  }
  nextSort(field) {
    let sh: SortHelper = this.sortHelper;
    this.sortHelper = sh.nextSort(field);
  }
  get sortedActions() {
    if (this.sortHelper.sort.sortBy === null) {
      this.transactionList = this.searchInputService.filterData(this.dontTouched, this.tableSearch.value);
      return this.transactionList
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
      this.transactionList = [...sorted];
      return this.transactionList
    }
  }
  async filterFields() {
    let isNewSearch = false
    for(const flag in this.isFilterActive){
      if(this.isFilterActive[flag]){
        isNewSearch = true
        this.isFilterActive[flag] = false;
      }
    }
    if(isNewSearch){
      await this.getTransactions();
    }
    for(const control in this.filter.value) {
      let value = this.filter.value[control];
      this.isFilterActive[control] = true;
      if (this.filter.value[control]&&control!=='dateStart'&&control!=='dateEnd'){
        this.dontTouched = this.dontTouched.filter(el=>el[control]!==null?`${el[control]}`.toLowerCase().includes(value.trim().toLowerCase()):false)
      }else if((control==='dateStart'&&this.filter.value[control])||(control==='dateEnd'&&this.filter.value[control])){
        this.dontTouched = this.dontTouched.filter(el=>{
          console.log(this.filter.value.dateStart,this.filter.value.dateEnd,el['timestamp']);
          return this.compareDates(this.filter.value.dateStart,this.filter.value.dateEnd,el['timestamp'])
        })
      }
    }
  }
  compareDates(dateValueStart,dateValueEnd,dateData){
    const startDate = dateValueStart? new Date(`${dateValueStart.year}-${dateValueStart.month}-${dateValueStart.day}`).getTime():null;
    const endDate = dateValueEnd? new Date(`${dateValueEnd.year}-${dateValueEnd.month}-${dateValueEnd.day}`).getTime():null;
    if(startDate===null){
      return dateData<=endDate
    }else if(endDate===null){
      return dateData>=startDate
    }else{
      return dateData>=startDate&&dateData<=endDate
    }
  }

  clearFilter() {
    this.filter.reset()
    this.getTransactions();
  }
}
