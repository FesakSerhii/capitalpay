import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";

const token = 'Bearer '+sessionStorage.getItem('token');

@Injectable({
  providedIn: 'root'
})
export class SupportService {

  constructor(public apiService: ApiService) { }

  getSupportList(){
    return this.apiService.postJwt('api','/support/list',token).toPromise();
  }
  getSupportListItem(id){
    return this.apiService.postJwt('api','/support/one',token,{id}).toPromise();
  }
  answer(data){
    return this.apiService.postJwt('api','/support/answer',token,data).toPromise();
  }
}
