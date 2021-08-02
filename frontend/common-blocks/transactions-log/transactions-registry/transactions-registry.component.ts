import { Component, OnInit } from '@angular/core';
import {PaymentsService} from '../../../projects/admin-panel/src/app/service/payments.service';
import {FormControl, FormGroup} from '@angular/forms';
import {ExtValidators} from '../../../src/app/validators/ext-validators';

@Component({
  selector: 'app-transactions-registry',
  templateUrl: './transactions-registry.component.html',
  styleUrls: ['./transactions-registry.component.scss']
})
export class TransactionsRegistryComponent implements OnInit {

  constructor(private paymentsService:PaymentsService) { }

  registryForm = new FormGroup({
    bclassd: new FormControl(),//*
    bclassd_merch: new FormControl(),//*
    date_last_downloads: new FormControl(),
    knp: new FormControl(),//
    knp_merch: new FormControl(),//*
    kobd: new FormControl(),//*
    kod: new FormControl(),//
    kod_merch: new FormControl(),//*
    lskor: new FormControl(),//*
    naznpl: new FormControl(),//*
    naznpl_merch: new FormControl(),//*
    order_number: new FormControl(),
    platel: new FormControl(),//;
    platel_merch: new FormControl(),//*
    poluch: new FormControl(),//*
    rnna: new FormControl('',[ExtValidators.IIN]),//;
    rnna_merch: new FormControl('',[ExtValidators.IIN]),//*
    rnnb: new FormControl('',[ExtValidators.IIN]),//*
  });
  periodForm = new FormGroup({
    start: new FormControl(),
    end: new FormControl()
  })
  registry:any = null;
  isEditMode: boolean = false;
  isEditModeMerch: boolean = false;

  ngOnInit() {
    this.getRegistryInfo()
  }
  async getRegistryInfo(){
  this.registryForm.patchValue({...await this.paymentsService.getRegistries()}.data)
  }
  saveEditedRegistry(){
    const values = this.registryForm.value;
    const arr = [];
    for(const field in values){
      arr.push({fieldName:field,fieldValue:values[field]})
    }
    this.paymentsService.editRegistries(arr).then(()=>{
      this.isEditMode = false;
      this.isEditModeMerch = false;
      this.getRegistryInfo()
    })
  }
  getDoc(){
    const value = this.periodForm.value
    const timestampAfter = new Date(`${value.start.year}-${value.start.month}-${value.start.day}`).getTime();
    const timestampBefore = new Date(`${value.end.year}-${value.end.month}-${value.end.day}`).getTime();
    this.paymentsService.getFile(timestampAfter,timestampBefore).then(resp=>{
      console.log(resp.headers.get('filename'));
      let fileLink = window.document.createElement('a');
      fileLink.href = URL.createObjectURL(resp);
      fileLink.download = `${value.start.day}-${value.start.month}-${value.start.day} - ${value.end.year}-${value.end.month}-${value.end.day}.txt`;
      fileLink.target = '_blank';
      fileLink.click();
    })
  }
}
