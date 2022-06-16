import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  constructor() { }

  get token(): string {
    return 'Bearer '+sessionStorage.getItem('token');
  }
}
