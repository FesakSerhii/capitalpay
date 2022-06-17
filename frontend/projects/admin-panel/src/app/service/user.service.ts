import { Injectable } from '@angular/core';
import {ApiService} from './api.service';
import {JwtHelperService} from "@auth0/angular-jwt";
import {TokenService} from "../../../../../src/app/service/token.service";

const helper = new JwtHelperService();

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(public apiService: ApiService,private tokenService:TokenService) { }
  getUserInfo(){
    try {
      return helper.decodeToken(sessionStorage.getItem('this.tokenService.token'));
    } catch (error) {

    }
  }
  getUserList(){
    return this.apiService.postJwt('api','/userlist/list',this.tokenService.token).toPromise();
  }
  createUser(newUserData){
    return this.apiService.postJwt('api','/userlist/newuser',this.tokenService.token,newUserData).toPromise();
  }
  getUserRolesList(){
    return this.apiService.postJwt('api','/userlist/rolelist',this.tokenService.token).toPromise();
  }
  changeUserRolesList(roles){
    return this.apiService.postJwt('api','/userlist/changeroles',this.tokenService.token,roles).toPromise();
  }
  editUserData(newUserData) {
    return this.apiService.postJwt('api', '/userlist/edituser', this.tokenService.token, newUserData).toPromise();
  }
  getUserData(id){
    return this.apiService.postJwt('api','/userlist/one',this.tokenService.token,{id}).toPromise();
  }
  deleteUser(userId){
    return this.apiService.postJwt('api','/userlist/deleteuser',this.tokenService.token,{userId}).toPromise();
  }
  getUsersCommissions(merchantId){
    return this.apiService.postJwt('api','/fee/cashbox/list',this.tokenService.token,{merchantId}).toPromise();
  }
  editUsersCommissions(data){
    return this.apiService.postJwt('api','/fee/cashbox/list/save',this.tokenService.token,data).toPromise();
  }
  blockUser(id){
    return this.apiService.postJwt('api',`/userlist/blockeuser?id=${id}`,this.tokenService.token).toPromise();
  }
  activateUser(id){
    return this.apiService.postJwt('api',`/userlist/activateuser?id=${id}`,this.tokenService.token).toPromise();
  }
}
