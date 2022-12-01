import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {TokenService} from "../../../../../src/app/service/token.service";


@Injectable({
  providedIn: 'root'
})
export class PaymentsService {

  constructor(public apiService: ApiService, private tokenService:TokenService) { }

  getMerchantPaymentMethods(merchantId){
    return this.apiService.postJwt('api','/paysystem/merchant/list',this.tokenService.token,{merchantId}).toPromise();
  }
  getPaymentMethods(){
    return this.apiService.postJwt('api','/paysystem/system/list',this.tokenService.token).toPromise();
  }
  editMerchantPaymentMethodsList(obj){
    return this.apiService.postJwt('api','/paysystem/merchant/edit',this.tokenService.token,obj).toPromise();
  }
  editEnable(obj){
    return this.apiService.postJwt('api','/paysystem/system/enable',this.tokenService.token,obj).toPromise();
  }
  getRegistries(){
    return this.apiService.postJwt('api','/paysystems/halyk/get',this.tokenService.token).toPromise();
  }
  editRegistries(fields){
    return this.apiService.postJwt('api','/paysystems/halyk/set',this.tokenService.token,{fields}).toPromise();
  }
  getTransactionsList(obj){
    const filter = Object.keys(obj).reduce((item, key) => (obj[key] === undefined || obj[key] === null || obj[key] === {} || key === 'dateStart' || key === 'dateEnd' ? item : {...item, [key]: obj[key]}), {});
    return this.apiService.postJwt('api','/payments/list/new',this.tokenService.token, filter).toPromise();
  }
  postMerchantNames(obj) {
    return this.apiService.postJwt('api','/payments/get/merchant-data',this.tokenService.token, obj).toPromise();
  }
  getTransactionDetails(guid){
    return this.apiService.postJwt('api','/payments/one',this.tokenService.token,{guid}).toPromise();
  }
  getFile(timestampAfter,timestampBefore){
    return this.apiService.postJwtFile('api','/paysystems/register/download',this.tokenService.token,{timestampAfter,timestampBefore}).toPromise();
  }
}
