import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";

const token = 'Bearer '+sessionStorage.getItem('token');

@Injectable({
  providedIn: 'root'
})
export class KycService {

  constructor(public apiService: ApiService) { }

  getKycInfo(merchantId){
    return this.apiService.postJwt('api','/merchantsettings/kyc/get',token,{merchantId}).toPromise();
  }
  setKycInfo(data){
    return this.apiService.postJwt('api','/merchantsettings/kyc/set',token,data).toPromise();
  }
}
