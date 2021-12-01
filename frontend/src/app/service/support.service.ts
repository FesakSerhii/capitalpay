import { Injectable } from '@angular/core';
import {ApiService} from './api.service';

const token = 'Bearer '+sessionStorage.getItem('token');

@Injectable({
  providedIn: 'root'
})
export class SupportService {

  constructor(public apiService: ApiService) { }

  getSupportListByMerchantId(){
    return this.apiService.postJwt('/api/v1/auth/support/list-by-merchantid', token).toPromise();
  }
  sendSupportRequest(data){
    return this.apiService.postJwt('/api/v1/auth/support/request', token,data).toPromise();
  }
  sendFeedback(data){
    return this.apiService.post('/help/feedback',data).toPromise();
  }

  getSupportList(){
    return this.apiService.postJwt('/support/list',token).toPromise();
  }
  setImportant(data){
    return this.apiService.postJwt('/support/status',token, data).toPromise();
  }
  getSupportListItem(id){
    return this.apiService.postJwt('/api/v1/auth/support/one',token,{id}).toPromise();
  }
  answer(data){
    return this.apiService.postJwt('/support/answer',token,data).toPromise();
  }
}
