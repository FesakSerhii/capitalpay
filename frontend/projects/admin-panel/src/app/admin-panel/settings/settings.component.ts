import { Component, OnInit } from '@angular/core';
import {CurrencyService} from '../../service/currency.service';
import {FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  activeTab = 'tab1';

  constructor(private currencyService:CurrencyService) { }
  currencyList:any=null;
  currenciesForm = new FormGroup({});
  isFirstLoad: boolean = true;
  ngOnInit(): void {
    this.getCurrencies()
  }
  async getCurrencies(){
    this.currencyService.getCurrencies().then(resp=>{
      this.currencyList = resp.data;
      for(const currency of this.currencyList){
        this.currenciesForm.addControl(currency.alpha, new FormControl(currency.enabled))
        // this.currenciesForm.controls[currency.alpha].setValue(currency.enabled, { emitEvent: false })
        if(this.isFirstLoad){
          this.currenciesForm.controls[currency.alpha].valueChanges.subscribe(next=>{
            const data = {
              paysystemId: currency.number,
              enabled: next
            }
            this.currencyService.editEnableCurrecies(data).then(()=>{
              this.getCurrencies();
            })
          })
        }
      }
      this.isFirstLoad = false;
    })
  }

}
