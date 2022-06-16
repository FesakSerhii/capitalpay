import { Injectable } from '@angular/core';
import {ApiService} from './api.service';
import {TokenService} from "../../../../../src/app/service/token.service";


@Injectable({
  providedIn: 'root'
})
export class StaticPageService {

  constructor(public apiService: ApiService, private tokenService:TokenService) { }
  getStaticPages(language ='RUS'){
    return this.apiService.postJwt('api','/staticpage/list',this.tokenService.token,{language}).toPromise();
  }
  getStaticPage(pageData){
    return this.apiService.postJwt('api','/staticpage/one',this.tokenService.token,pageData).toPromise();
  }
  saveStaticPages(language ='RUS'){
    return this.apiService.postJwt('api','/staticpage/save',this.tokenService.token,language).toPromise();
  }
  deleteStaticPages(tag){
    return this.apiService.postJwt('api','/staticpage/delete',this.tokenService.token, {tag}).toPromise();
  }

}
