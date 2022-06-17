import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {TokenService} from "../../../../../src/app/service/token.service";


@Injectable({
  providedIn: 'root'
})
export class CurrencyService {

  constructor(public apiService: ApiService,private tokenService:TokenService) { }

  getMerchantCurrencies(merchantId){
    return this.apiService.postJwt('api','/currency/merchant/list',this.tokenService.token,{merchantId}).toPromise();
  }
  getCurrencies(){
    return this.apiService.postJwt('api','/currency/system/list',this.tokenService.token).toPromise();
  }
  editEnableCurrecies(data){
    return this.apiService.postJwt('api','/currency/system/edit',this.tokenService.token,data).toPromise();
  }
  editUsersCurrenciesList(obj){
    return this.apiService.postJwt('api','/currency/merchant/edit',this.tokenService.token,obj).toPromise();
  }
}
