import { Injectable } from '@angular/core';
import {Subject} from 'rxjs';
import {SortHelper} from '../helper/sort-helper';

@Injectable({
  providedIn: 'root'
})
export class SearchInputService {
  sortHelper:any;
  constructor() {
    this.sortHelper = new SortHelper();
  }
  private searchInputSource = new Subject<string>();
  private searchInputDestination = new Subject<string>();
  searchInputValue$ = this.searchInputSource.asObservable();

  changeValue(searchValue: string) {
    this.searchInputSource.next(searchValue);
  }
  filterData(data,value){
    if (!value) return data;
    return data.filter((el)=>{
      for(let item in el){
        if(el[item]&&String(el[item]).toLowerCase().indexOf(value.toLowerCase())!==-1){
          return el
        }
      }
    })
  }

  filterFormArray(data,value){
    if (!value) return data;
    return data.filter( it => {
      return it.value.device_id.toLowerCase().includes(value);
    });
  }

  sortedActions(list,search) {
    if (this.sortHelper.sort.sortBy === null) {
      return this.filterData(list, search)
    } else {
      let sorted = list.sort(
        (a, b) => {
          let aField = a[this.sortHelper.sort.sortBy];
          let bField = b[this.sortHelper.sort.sortBy];
          let res = aField == bField ? 0 : (aField > bField ? 1 : -1);
          return this.sortHelper.sort.increase ? res : -res;
        }
      )
      sorted = this.filterData(sorted, search);
      return sorted
    }
  }
}
