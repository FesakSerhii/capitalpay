import { Injectable } from '@angular/core';
import {ApiService} from './api.service';

const token = 'Bearer '+sessionStorage.getItem('token');

@Injectable({
  providedIn: 'root'
})
export class FileService {

  constructor(public apiService: ApiService) { }

  sendFile(fileToUpload){
    const multipartFile = new FormData();
    multipartFile.append('multipartFile', fileToUpload, fileToUpload.name);
    return this.apiService.postFormData('api','/file/upload', token, multipartFile).toPromise();
  }
}
