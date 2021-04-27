import { Injectable } from '@angular/core';
import {ApiService} from './api.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(public apiService: ApiService) { }
  login(username: string,password: string){
    return this.apiService.log('/login', {username,password}).toPromise();
  }
}
