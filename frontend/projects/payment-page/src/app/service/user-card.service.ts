import {Injectable} from '@angular/core';
import {ApiService} from './api.service';


@Injectable({
  providedIn: 'root'
})
export class UserCardService {

  constructor( public apiService: ApiService) { }

  registerCard(cardNumber,expireYear,expireMonth,cvv2Code,merchantId,cashBoxId,parameters) {
    return this.apiService.post('api','/client-card/register', {cardNumber,expireYear,expireMonth,cvv2Code,merchantId,cashBoxId,parameters}).toPromise();
  }
  registerCardWithBank(merchantId) {
    return this.apiService.post('api','/user-card/register-with-bank',{merchantId}).toPromise()
  }
  cardCheckValidity(cardId) {
    return this.apiService.post('api',`/client-card/check-validity/${cardId}`).toPromise();
  }
  getCardInfo(token) {
    return this.apiService.post('api',`/client-card?token=${token}`).toPromise();
  }
  getCashBoxInfo(cashBoxId) {
    return this.apiService.post('api',`/cashboxsetting/get`,{cashBoxId}).toPromise();
  }
  clientCardList(token) {
    return this.apiService.post('api',`/client-card/client-cards`).toPromise();
  }
  sendAnonP2pToMerchant(data) {
    return this.apiService.post('api',`/p2p/send-anonymous-p2p-to-merchant`,data).toPromise();
  }
  getByPaymentId(paymentId) {
    return this.apiService.post('api',`/cashboxsetting/get-by-paymentid?paymentId=${paymentId}`).toPromise();
  }
  getPublicInfo(id) {
    return this.apiService.get('api', '/merchant-link/get-public-info/'+id).toPromise()
  }
  postCreateByLink(obj) {
    return this.apiService.post('api', '/payment-link/create-by-link', obj).toPromise()
  }
}
