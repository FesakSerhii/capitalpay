import { Injectable } from '@angular/core';
import {ApiService} from "../../../projects/admin-panel/src/app/service/api.service";
import {TokenService} from "./token.service";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  constructor(public apiService: ApiService,private tokenService:TokenService) { }

  postPaymentLinkCreate(data) {
    return this.apiService.postJwt('api','/payment-link/create', this.tokenService.token, data).toPromise();
  }
  getPaymentLinkList() {
    return this.apiService.get('api','/payment-link/list', this.tokenService.token).toPromise();
  }
  postPaymentLinkRenew(linkId) {
    return this.apiService.postJwt('api','/payment-link/renew/'+linkId, this.tokenService.token).toPromise();
  }
}
