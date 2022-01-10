import {Component, OnInit, ViewChild} from '@angular/core';
import {PaymentsService} from '../../../projects/admin-panel/src/app/service/payments.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ExtValidators} from '../../../src/app/validators/ext-validators';
import {HttpResponse} from '@angular/common/http';
import {dateCompareValidator} from '../../validators/dateCompareValidator';
import {MassageModalComponent} from '../../massage-modal/massage-modal.component';

@Component({
  selector: 'app-transactions-registry',
  templateUrl: './transactions-registry.component.html',
  styleUrls: ['./transactions-registry.component.scss']
})
export class TransactionsRegistryComponent implements OnInit {

  constructor(private paymentsService: PaymentsService) {
  }
  @ViewChild("invalidDatesModalContent", {static: false}) invalidDatesModal: MassageModalComponent;

  dates = [
    {
      title: 'За текущий день',
      value: 'getToday'
    },
    {
      title: 'За текущую неделю',
      value: 'getClosestWeek'
    },
    {
      title: 'За текущий месяц',
      value: 'getClosestMonth'
    },
    {
      title: 'За текущий квартал',
      value: 'getClosestQuarter'
    },
    {
      title: 'За текущие полугодие',
      value: 'getClosestHalfYear'
    },
    {
      title: 'За текущий год',
      value: 'getClosestYear'
    }
  ];
  registryForm = new FormGroup({
    bclassd: new FormControl(),
    bclassd_merch: new FormControl(),
    date_last_downloads: new FormControl(),
    knp: new FormControl(),
    knp_merch: new FormControl(),
    kobd: new FormControl(),
    bik: new FormControl(),
    kod: new FormControl(),//
    kod_merch: new FormControl(),
    lskor: new FormControl(),
    naznpl: new FormControl(),
    naznpl_merch: new FormControl(),
    order_number: new FormControl(),
    platel: new FormControl(),
    platel_merch: new FormControl(),
    poluch: new FormControl(),
    rnna: new FormControl('', [ExtValidators.IIN]),
    rnna_merch: new FormControl('', [ExtValidators.IIN]),
    rnnb: new FormControl('', [ExtValidators.IIN]),
  });
  periodForm = new FormGroup({
    start: new FormControl(),
    end: new FormControl(),
    dateFilter: new FormControl(),
  })
  registry: any = null;
  isEditMode: boolean = false;
  isEditModeMerch: boolean = false;

  ngOnInit() {
    let dateFields = {
      start: "end",
    };
    for(let v in dateFields){
      this.periodForm.get(v).setValidators([Validators.required, dateCompareValidator(this.periodForm, dateFields[v])]);
      this.periodForm.get(dateFields[v]).setValidators([Validators.required, dateCompareValidator(this.periodForm, v, true)]);
    }
    this.getRegistryInfo()
    this.periodForm.controls.dateFilter.valueChanges.subscribe((val) => {
      this.formTerms(val)
    })
  }

  async getRegistryInfo() {
    this.registryForm.patchValue({...await this.paymentsService.getRegistries()}.data)
  }

  saveEditedRegistry() {
    const values = this.registryForm.value;
    const arr = [];
    for (const field in values) {
      arr.push({fieldName: field, fieldValue: values[field]})
    }
    this.paymentsService.editRegistries(arr).then(() => {
      this.isEditMode = false;
      this.isEditModeMerch = false;
      this.getRegistryInfo()
    })
  }

  getDoc() {
    const value = this.periodForm.value
    const timestampAfter = new Date(`${value.start.year}-${value.start.month}-${value.start.day}`).getTime();
    const timestampBefore = new Date(`${value.end.year}-${value.end.month}-${value.end.day}`).getTime();
    this.paymentsService.getFile(timestampAfter, timestampBefore).then((resp: HttpResponse<any>) => {
      // +86340
      console.log(resp);
      console.log(resp.headers.get('Content-Disposition'));
      let fileLink = window.document.createElement('a');
      fileLink.href = URL.createObjectURL(new Blob([resp.body]));
      fileLink.download = `9120${new Date().getMonth() + 1}${new Date().getDate()}.txt`;
      fileLink.target = '_blank';
      fileLink.click();
    })
  }

  formTerms(termValue) {
    const todayDate = new Date();
    this.periodForm.controls.end.setValue({day: todayDate.getDate(), month: todayDate.getMonth() + 1, year: todayDate.getFullYear(),})
    this.setStartDay(todayDate)
    if(termValue!=='getToday'){
      this.setStartDay(this[termValue](todayDate))
    }
  }

  setStartDay(today) {
    this.periodForm.controls.start.setValue({day: today.getDate(), month: today.getMonth() + 1, year: today.getFullYear()})
  }

  getClosestQuarter(today) {
    if ((today.getMonth()+1)% 3 === 0 || (today.getMonth()) === 0) {
      return new Date(today.getFullYear(), today.getMonth(), 1)
    } else {
      today.setMonth(today.getMonth()-1)
      return this.getClosestQuarter(today)
    }
  }

  getClosestWeek(today) {
    const date = new Date(today)
    if (date.getDay() == 1) {
      return date
    } else {
      return this.getClosestWeek(date.setDate(date.getDate() - 1))
    }
  }

  getClosestYear(today) {
    return new Date(today.getFullYear(), 0, 1,)
  }
  getClosestMonth(today) {
    return new Date(today.getFullYear(), today.getMonth(), 1,)
  }

  getClosestHalfYear(today) {
    return new Date(today.setMonth(today.getMonth() - 6))
  }
  dateInvalid(start,end){
    if(this.periodForm.get(start).value!==null&&this.periodForm.get(end).value!==null){
      setTimeout(() => {
        if (this.periodForm.get(start).invalid || this.periodForm.get(end).invalid) {
          this.invalidDatesModal.open()
          this.periodForm.get(start).reset();
          this.periodForm.get(end).reset();
        }
      }, 200);
    }
  }
}
