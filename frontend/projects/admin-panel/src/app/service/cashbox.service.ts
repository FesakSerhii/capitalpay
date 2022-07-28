import { Injectable } from '@angular/core';
import {ApiService} from './api.service';
import {TokenService} from "../../../../../src/app/service/token.service";


@Injectable({
  providedIn: 'root'
})
export class CashBoxService {

  constructor(public apiService: ApiService,private tokenService:TokenService) { }
  addCashBox(data){
    return this.apiService.postJwt('api','/cashbox/create',this.tokenService.token,data).toPromise();
  }
  getCashBoxListAll(){
    return this.apiService.postJwt('api','/cashbox/all',this.tokenService.token).toPromise();
  }
  getCashBoxListByMerchantId(merchantId){
    return this.apiService.postJwt('api','/cashbox/list',this.tokenService.token,{merchantId}).toPromise();
  }
  deleteCashBox(cashboxId){
    return this.apiService.postJwt('api','/cashbox/delete',this.tokenService.token,{cashboxId}).toPromise();
  }
  editCashBox(data){
    return this.apiService.postJwt('api','/cashbox/changename',this.tokenService.token,data).toPromise();
  }
  getCashBoxInfo(cashBoxId){
    return this.apiService.post('api','/cashboxsetting/get', {cashBoxId}).toPromise();
  }
  editCashBoxInfo(data){
    return this.apiService.post('api','/cashboxsetting/set', data).toPromise();
  }
  getCashBoxPaymentList(cashboxId){
    return this.apiService.postJwt('api','/paysystem/cashbox/list',this.tokenService.token, {cashboxId}).toPromise();
  }
  getCashBoxCurrencyList(cashboxId){
    return this.apiService.postJwt('api','/currency/cashbox/list',this.tokenService.token, {cashboxId}).toPromise();
  }
  editCashBoxPaymentList(data){
    return this.apiService.postJwt('api','/paysystem/cashbox/edit',this.tokenService.token,data).toPromise();
  }
  editEnableCurrecies(data){
    return this.apiService.postJwt('api','/currency/cashbox/edit',this.tokenService.token,data).toPromise();
  }
}
