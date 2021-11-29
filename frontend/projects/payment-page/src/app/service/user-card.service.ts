import {Injectable} from '@angular/core';
import {ApiService} from './api.service';


@Injectable({
  providedIn: 'root'
})
export class UserCardService {

  constructor( public apiService: ApiService) { }

  registerCard(cardNumber,expireYear,expireMonth,cvv2Code) {
    return this.apiService.post('/api/v1/user-card/register', {cardNumber,expireYear,expireMonth,cvv2Code}).toPromise();
  }
  cardCheckValidity(cardId) {
    return this.apiService.post(`/api/v1/user-card/check-validity/${cardId}`).toPromise();
  }
  getCardInfo(token) {
    return this.apiService.post(`/api/v1/user-card?token=${token}`).toPromise();
  }
  clientCardList(token) {
    return this.apiService.post(`/api/v1/user-card/client-cards`).toPromise();
  }
}
