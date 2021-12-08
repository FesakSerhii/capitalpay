import {Injectable} from '@angular/core';
import {ApiService} from './api.service';


@Injectable({
  providedIn: 'root'
})
export class UserCardService {

  constructor( public apiService: ApiService) { }

  registerCard(cardNumber,expireYear,expireMonth,cvv2Code,merchantId) {
    return this.apiService.post('/client-card/register', {cardNumber,expireYear,expireMonth,cvv2Code,merchantId}).toPromise();
  }
  cardCheckValidity(cardId) {
    return this.apiService.post(`/client-card/check-validity/${cardId}`).toPromise();
  }
  getCardInfo(token) {
    return this.apiService.post(`/client-card?token=${token}`).toPromise();
  }
  clientCardList(token) {
    return this.apiService.post(`/client-card/client-cards`).toPromise();
  }
}
