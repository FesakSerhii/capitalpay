import { Injectable } from '@angular/core';
import {ApiService} from './api.service';
import {JwtHelperService} from "@auth0/angular-jwt";

const helper = new JwtHelperService();
const token = 'Bearer '+sessionStorage.getItem('token');
const user = helper.decodeToken(sessionStorage.getItem('token'))

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(public apiService: ApiService) { }

  getUserList(){
    return this.apiService.postJwt('api','/userlist/list',token).toPromise();
  }
  createUser(newUserData){
    return this.apiService.postJwt('api','/userlist/newuser',token,newUserData).toPromise();
  }
  getUserRolesList(){
    return this.apiService.postJwt('api','/userlist/rolelist',token).toPromise();
  }
  editUserData(newUserData) {
    return this.apiService.postJwt('api', '/userlist/edituser', token, newUserData).toPromise();
  }
  getUserData(id){
    return this.apiService.postJwt('api','/userlist/one',token,{id}).toPromise();
  }
  getUserInfo(){
    return user;
  }
}
