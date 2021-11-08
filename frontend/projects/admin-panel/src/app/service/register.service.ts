import { Injectable } from '@angular/core';
import {ApiService} from './api.service';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(public apiService: ApiService) { }
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
    return this.apiService.post('api','/signup/step3', {code, password, phone: username}).toPromise();
  }
  // tslint:disable-next-line:typedef
  confirmPhone(code: string){
    return this.apiService.post('api','/signup/step5', {code}).toPromise();
  }
}
