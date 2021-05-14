import { Injectable } from '@angular/core';
import {ApiService} from './api.service';

const token = 'Bearer '+sessionStorage.getItem('token');
@Injectable({
  providedIn: 'root'
})
export class StaticPageService {

  constructor(public apiService: ApiService) { }
  getStaticPages(language ='rus'){
    return this.apiService.postJwt('api','/staticpage/list',token,{language}).toPromise();
  }
  saveStaticPages(language ='rus'){
    return this.apiService.postJwt('api','/staticpage/save',token,language).toPromise();
  }
  deleteStaticPages(tag){
    return this.apiService.postJwt('api','/staticpage/delete',token, {tag}).toPromise();
  }

}
