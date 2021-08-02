import { Injectable } from '@angular/core';
import {ApiService} from './api.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(public apiService: ApiService) { }
  login(username: string,password: string,sms:string = null){
    return this.apiService.log('/login', {username,password,sms}).toPromise();
  }
  logout(){
    sessionStorage.clear()
  }
  checkSessions(){
    return this.apiService.post('/api/v1/userlist/numbersSession').toPromise();
  }
}
