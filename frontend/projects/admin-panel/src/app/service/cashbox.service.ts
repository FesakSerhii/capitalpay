import { Injectable } from '@angular/core';
import {ApiService} from './api.service';

const token = 'Bearer '+sessionStorage.getItem('token');

@Injectable({
  providedIn: 'root'
})
export class CashBoxService {

  constructor(public apiService: ApiService) { }
  addCashBox(data){
    return this.apiService.postJwt('api','/cashbox/create',token,data).toPromise();
  }
  getCashBoxListAll(){
    return this.apiService.postJwt('api','/cashbox/all',token).toPromise();
  }
  getCashBoxListByMerchantId(merchantId){
    return this.apiService.postJwt('api','/cashbox/list',token,{merchantId}).toPromise();
  }
  deleteCashBox(cashboxId){
    return this.apiService.postJwt('api','/cashbox/delete',token,{cashboxId}).toPromise();
  }
  editCashBox(data){
    return this.apiService.postJwt('api','/cashbox/changename',token,data).toPromise();
  }
  getCashBoxInfo(cashBoxId){
    return this.apiService.post('api','/cashboxsetting/get', {cashBoxId}).toPromise();
  }
  editCashBoxInfo(data){
    return this.apiService.post('api','/cashboxsetting/set', data).toPromise();
  }
  getCashBoxPaymentList(cashboxId){
    return this.apiService.postJwt('api','/paysystem/cashbox/list',token, {cashboxId}).toPromise();
  }
  getCashBoxCurrencyList(cashboxId){
    return this.apiService.postJwt('api','/currency/cashbox/list',token, {cashboxId}).toPromise();
  }
  editCashBoxPaymentList(data){
    return this.apiService.postJwt('api','/paysystem/cashbox/edit',token,data).toPromise();
  }
  editEnableCurrecies(data){
    return this.apiService.postJwt('api','/currency/cashbox/edit',token,data).toPromise();
  }
}
