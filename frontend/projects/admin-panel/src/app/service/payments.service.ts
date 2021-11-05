import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
const token = 'Bearer '+sessionStorage.getItem('token');
@Injectable({
  providedIn: 'root'
})
export class PaymentsService {

  constructor(public apiService: ApiService) { }

  getMerchantPaymentMethods(merchantId){
    return this.apiService.postJwt('api','/paysystem/merchant/list',token,{merchantId}).toPromise();
  }
  getPaymentMethods(){
    return this.apiService.postJwt('api','/paysystem/system/list',token).toPromise();
  }
  editMerchantPaymentMethodsList(obj){
    return this.apiService.postJwt('api','/paysystem/merchant/edit',token,obj).toPromise();
  }
  editEnable(obj){
    return this.apiService.postJwt('api','/paysystem/system/enable',token,obj).toPromise();
  }
  getRegistries(){
    return this.apiService.postJwt('api','/paysystems/halyk/get',token).toPromise();
  }
  editRegistries(fields){
    return this.apiService.postJwt('api','/paysystems/halyk/set',token,{fields}).toPromise();
  }
  getTransactionsList(){
    return this.apiService.postJwt('api','/payments/list',token).toPromise();
  }
  getTransactionDetails(guid){
    return this.apiService.postJwt('api','/payments/one',token,{guid}).toPromise();
  }
  getFile(timestampAfter,timestampBefore){
    return this.apiService.postJwtFile('api','/paysystems/register/download',token,{timestampAfter,timestampBefore}).toPromise();
  }

}
