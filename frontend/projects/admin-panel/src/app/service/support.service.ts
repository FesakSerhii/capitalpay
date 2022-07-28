import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {TokenService} from "../../../../../src/app/service/token.service";




@Injectable({
  providedIn: 'root'
})
export class SupportService {

  constructor(public apiService: ApiService, private tokenService:TokenService) { }

  getSupportList(){
    return this.apiService.postJwt('api','/support/list',this.tokenService.token).toPromise();
  }
  setImportant(data){
    return this.apiService.postJwt('api','/support/status',this.tokenService.token, data).toPromise();
  }
  getSupportListItem(id){
    return this.apiService.postJwt('api','/support/one',this.tokenService.token,{id}).toPromise();
  }
  answer(data){
    return this.apiService.postJwt('api','/support/answer',this.tokenService.token,data).toPromise();
  }
}
