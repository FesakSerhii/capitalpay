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
}
