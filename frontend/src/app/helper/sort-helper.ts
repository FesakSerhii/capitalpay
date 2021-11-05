import {ParamMap} from '@angular/router';

export class SortHelper {
  constructor(public sortBy:string = null, public increase: boolean = null) {
  }

  loadFromParams(params: ParamMap, defaultValue: {sortBy: string, increase: boolean} = {sortBy: null, increase: null}) {
    this.sortBy = params.get("sortBy") ? params.get("sortBy"): defaultValue.sortBy;
    this.increase = params.get("increase") ? params.get("increase") == "true": defaultValue.increase;
  }

  public get sort() {
    return {sortBy: this.sortBy, increase: this.increase}
  }

  public nextSort(sortBy: string): SortHelper {
    let newSortBy: string = null;
    let newIncrease: boolean = null;
    if (sortBy === this.sortBy) {
      if (this.increase === true) {
        newSortBy = this.sortBy;
        newIncrease = !this.increase;
      }
    } else {
      newSortBy = sortBy;
      newIncrease = true;
    }

    return new SortHelper(newSortBy, newIncrease);
  }
}
