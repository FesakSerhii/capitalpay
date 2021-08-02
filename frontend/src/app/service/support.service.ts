import { Injectable } from '@angular/core';
import {ApiService} from './api.service';

const token = 'Bearer '+sessionStorage.getItem('token');

@Injectable({
  providedIn: 'root'
})
export class SupportService {

  constructor(public apiService: ApiService) { }

  sendSupportRequest(data){
    return this.apiService.postJwt('/api/v1/support/request', token,data).toPromise();
  }
  sendFeedback(data){
    return this.apiService.post('/help/feedback',data).toPromise();
  }
}
