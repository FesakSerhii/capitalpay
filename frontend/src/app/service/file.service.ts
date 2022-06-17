import { Injectable } from '@angular/core';
import {ApiService} from './api.service';
import {TokenService} from "./token.service";


@Injectable({
  providedIn: 'root'
})
export class FileService {

  constructor(public apiService: ApiService, private tokenService:TokenService) { }

  sendFile(fileToUpload){
    const multipartFile = new FormData();
    multipartFile.append('multipartFile', fileToUpload, fileToUpload.name);
    return this.apiService.postFormData('api','/file/upload', this.tokenService.token, multipartFile).toPromise();
  }
}
