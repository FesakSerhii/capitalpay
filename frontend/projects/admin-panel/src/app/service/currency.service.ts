import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";

const token = 'Bearer '+sessionStorage.getItem('token');

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {

  constructor(public apiService: ApiService) { }

  getMerchantCurrencies(merchantId){
    return this.apiService.postJwt('api','/currency/merchant/list',token,{merchantId}).toPromise();
  }
  getCurrencies(){
    return this.apiService.postJwt('api','/currency/system/list',token).toPromise();
  }
  editEnableCurrecies(data){
    return this.apiService.postJwt('api','/paysystem/system/enable',token,data).toPromise();
  }
  editUsersCurrenciesList(obj){
    return this.apiService.postJwt('api','/currency/merchant/edit',token,obj).toPromise();
  }
}
