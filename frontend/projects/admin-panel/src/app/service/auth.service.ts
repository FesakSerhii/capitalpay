import { Injectable } from '@angular/core';
import {ApiService} from './api.service';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Config} from 'protractor';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(public apiService: ApiService,private http: HttpClient) { }
  login(username: string,password: string,sms:string = null){
    return this.apiService.log('login','/login', {username,password,sms}).toPromise();
  }
}
