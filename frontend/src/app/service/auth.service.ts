import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Subject } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public tokenStateChange = new Subject<any>()

  constructor(
    public apiService: ApiService,
    public jwtHelper: JwtHelperService
  ) { }

  login(username: string, password: string, sms: string = null) {
    return this.apiService.log('/login', { username, password, sms }).toPromise();
  }

  logout() {
    sessionStorage.clear()
  }
  
  checkSessions() {
    return this.apiService.post('/api/v1/userlist/numbersSession').toPromise();
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
