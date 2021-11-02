import { Injectable } from '@angular/core';
import {ApiService} from './api.service';
import {JwtHelperService} from "@auth0/angular-jwt";

const helper = new JwtHelperService();
const token = 'Bearer '+sessionStorage.getItem('token');

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(public apiService: ApiService) { }
  getUserInfo(){
    try {
      return helper.decodeToken(sessionStorage.getItem('token'));
    } catch (error) {
      
    }
  }
  getUserList(){
    return this.apiService.postJwt('api','/userlist/list',token).toPromise();
  }
  createUser(newUserData){
    return this.apiService.postJwt('api','/userlist/newuser',token,newUserData).toPromise();
  }
  getUserRolesList(){
    return this.apiService.postJwt('api','/userlist/rolelist',token).toPromise();
  }
  changeUserRolesList(roles){
    return this.apiService.postJwt('api','/userlist/changeroles',token,roles).toPromise();
  }
  editUserData(newUserData) {
    return this.apiService.postJwt('api', '/userlist/edituser', token, newUserData).toPromise();
  }
  getUserData(id){
    return this.apiService.postJwt('api','/userlist/one',token,{id}).toPromise();
  }
  deleteUser(userId){
    return this.apiService.postJwt('api','/userlist/deleteuser',token,{userId}).toPromise();
  }
  getUsersCommissions(merchantId){
    return this.apiService.postJwt('api','/fee/cashbox/list',token,{merchantId}).toPromise();
  }
  editUsersCommissions(data){
    return this.apiService.postJwt('api','/fee/cashbox/list/save',token,data).toPromise();
  }
  blockUser(id){
    return this.apiService.postJwt('api',`/userlist/blockeuser?id=${id}`,token).toPromise();
  }
  activateUser(id){
    return this.apiService.postJwt('api',`/userlist/activateuser?id=${id}`,token).toPromise();
  }
}
