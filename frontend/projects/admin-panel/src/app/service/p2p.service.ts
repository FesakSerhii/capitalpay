import { Injectable } from '@angular/core';
import {ApiService} from './api.service';

@Injectable({
  providedIn: 'root'
})
export class P2pService {

  constructor(public apiService: ApiService) { }

  registerCard(cardNumber,expireYear,expireMonth,cvv2Code,merchantId) {
    return this.apiService.post('api','/user-card/register', {cardNumber,expireYear,expireMonth,cvv2Code,merchantId})
  }
  cardCheckValidity(cardId) {
    return this.apiService.post('api',`/user-card/check-validity/${cardId}`).toPromise();
  }
  getCardInfo(token) {
    return this.apiService.post('api',`/user-card?token=${token}`).toPromise();
  }
  getP2pInfo(merchantId) {
    return this.apiService.post('api',`/p2p-settings/get?merchantId=${merchantId}`).toPromise();
  }
  setP2p(data) {
    return this.apiService.post('api',`/p2p-settings/set`,data).toPromise();
  }
  setP2pCashBox(data) {
    return this.apiService.post('api',`/cashbox/set-p2p-settings`,data).toPromise();
  }
  setDefaultCard(data) {
    return this.apiService.post('api',`/user-card/change-default-card`,data)
  }
  setCashBoxCard(data) {
    return this.apiService.post('api',`/cashbox/set-card`,data)
  }
  clientCardList(merchantId) {
    return this.apiService.post('api',`/user-card/list?merchantId=${merchantId}`).toPromise();
  }
  getCashBoxP2pInfo(cashBoxId) {
    return this.apiService.post('api',`/cashbox/get-p2p-settings?cashBoxId=${cashBoxId}`).toPromise();
  }
}
