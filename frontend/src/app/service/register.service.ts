import { Injectable } from '@angular/core';
import {ApiService} from './api.service';
import {TokenService} from "./token.service";


@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(public apiService: ApiService, private tokenService:TokenService) { }
  // tslint:disable-next-line:typedef
  postEmail(email: string){
    return this.apiService.post('api','/signup/step1', {email}).toPromise();
  }
  // tslint:disable-next-line:typedef
  confirmEmail(code: string){
    return this.apiService.post('api','/signup/step2', {code}).toPromise();
  }
  // tslint:disable-next-line:typedef
  sendPassword(code: string, password: string, phone: string){
    return this.apiService.post('api','/signup/step3', {code, password, phone: phone}).toPromise();
  }
  // tslint:disable-next-line:typedef
  changePassword(oldPassword,newPassword){
    return this.apiService.postJwt('/password/new', this.tokenService.token,{oldPassword,newPassword}).toPromise();
  }
  // tslint:disable-next-line:typedef
  twoFactorAuthAvailable(){
    return this.apiService.postJwt('api','/twofactorauth/available', this.tokenService.token).toPromise();
  }
  // tslint:disable-next-line:typedef
  twoFactorAuthSet(enable){
    return this.apiService.postJwt('api','/twofactorauth/set', this.tokenService.token,{enable}).toPromise();
  }
  // tslint:disable-next-line:typedef
  confirmPhone(code: string){
    return this.apiService.post('api','/signup/step5', {code}).toPromise();
  }
}
