import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {TokenService} from "../../../../../src/app/service/token.service";


@Injectable({
  providedIn: 'root'
})
export class KycService {

  constructor(public apiService: ApiService,private tokenService:TokenService) { }

  getKycInfo(merchantId){
    return this.apiService.postJwt('api','/merchantsettings/kyc/get',this.tokenService.token,{merchantId}).toPromise();
  }
  setKycInfo(data){
    return this.apiService.postJwt('api','/merchantsettings/kyc/set',this.tokenService.token,data).toPromise();
  }
}
