import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Config } from 'protractor';
import { Observable, Subject } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public tokenStateChange = new Subject<any>()

  constructor(
    public apiService: ApiService, 
    private http: HttpClient,
    public jwtHelper: JwtHelperService
  ) { }

  login(username: string, password: string, sms: string = null) {
    return this.apiService.log('login', '/login', { username, password, sms }).toPromise();
  }

  checkToken() {
    const token = sessionStorage.getItem('token');
    if (!token) return false
    try {
      return !this.jwtHelper.isTokenExpired(token)
    } catch (error) {
      return false
    }
    
  }
}
