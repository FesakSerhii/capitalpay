import { Injectable } from '@angular/core';
import {ApiService} from './api.service';

const token = 'Bearer '+sessionStorage.getItem('token');

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(public apiService: ApiService) { }
  // tslint:disable-next-line:typedef
  postEmail(email: string){
    return this.apiService.post('/v1/signup/step1', {email}).toPromise();
  }
  // tslint:disable-next-line:typedef
  confirmEmail(code: string){
    return this.apiService.post('/v1/signup/step2', {code}).toPromise();
  }
  // tslint:disable-next-line:typedef
  sendPassword(code: string, password: string, phone: string){
    return this.apiService.post('/v1/signup/step3', {code, password, phone: phone}).toPromise();
  }
  // tslint:disable-next-line:typedef
  changePassword(oldPassword,newPassword){
    return this.apiService.postJwt('/api/v1/password/new', token,{oldPassword,newPassword}).toPromise();
  }
  // tslint:disable-next-line:typedef
  twoFactorAuthAvailable(){
    return this.apiService.postJwt('/api/v1/twofactorauth/available', token).toPromise();
  }
  // tslint:disable-next-line:typedef
  twoFactorAuthSet(enable){
    return this.apiService.postJwt('/api/v1/twofactorauth/set', token,{enable}).toPromise();
  }
  // tslint:disable-next-line:typedef
  confirmPhone(code: string){
    return this.apiService.post('/v1/signup/step5', {code}).toPromise();
  }
}
