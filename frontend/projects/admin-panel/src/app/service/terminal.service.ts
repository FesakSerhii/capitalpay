import {Injectable} from '@angular/core';
import {ApiService} from "./api.service";
import {TokenService} from "../../../../../src/app/service/token.service";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class TerminalService {

  constructor(private api: ApiService, private tokenService:TokenService) {
  }

  getTerminals() {
    return this.api.postJwt("api", "/terminal/all", this.tokenService.token).pipe(
      map(resp => resp.data.sort((t1, t2) => t1.id - t2.id))
    );
  }

  getFreeTerminals() {
    return this.api.postJwt("api", "/terminal/free", this.tokenService.token).pipe(
      map(resp => resp.data)
    );
  }

  createTerminal(data) {
    return this.api.postJwt("api", "/terminal/create", this.tokenService.token, data);
  }

  updateTerminal(data) {
    return this.api.postJwt("api", "/terminal/edit", this.tokenService.token, data).pipe(
      map(resp => resp.data)
    );
  }

  deleteTerminal(id) {
    return this.api.postJwt("api", "/terminal/delete", this.tokenService.token, {id});
  }

  getMerchantTerminal(id) {
    return this.api.postJwt("api", "/merchant-terminal-settings/get", this.tokenService.token, {id}).pipe(
      map(resp => resp.data)
    );
  }

  updateMerchantTerminal(data) {
    return this.api.postJwt("api", "/merchant-terminal-settings/set", this.tokenService.token, data).pipe(
      map(resp => resp.data)
    );
  }

}
