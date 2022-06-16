import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {TokenService} from "../../../../../src/app/service/token.service";

const token = 'Bearer '+sessionStorage.getItem('token');

@Injectable({
  providedIn: 'root'
})
export class SupportService {

  constructor(public apiService: ApiService, private tokenService:TokenService) { }

  getSupportList(){
    return this.apiService.postJwt('api','/support/list',token).toPromise();
  }
  setImportant(data){
    return this.apiService.postJwt('api','/support/status',token, data).toPromise();
  }
  getSupportListItem(id){
    return this.apiService.postJwt('api','/support/one',token,{id}).toPromise();
  }
  answer(data){
    return this.apiService.postJwt('api','/support/answer',token,data).toPromise();
  }
}
