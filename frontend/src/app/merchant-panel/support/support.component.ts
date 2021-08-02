import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {FileService} from '../../service/file.service';
import {SupportService} from '../../service/support.service';

@Component({
  selector: 'app-support',
  templateUrl: './support.component.html',
  styleUrls: ['./support.component.scss']
})
export class SupportComponent implements OnInit {

  constructor(private fileService: FileService, private supportService:SupportService) { }
  activeTab:string='tab1';
  questionForm= new FormGroup({
    "theme": new FormControl(),
    "subject": new FormControl(''),
    "text": new FormControl()
  });
  fileToUpload: File = null;
  fileList: any[]=[];
  ngOnInit(): void {
  }

  send() {
  let request = {
    "fileList": this.fileList
  }
  Object.assign(request,this.questionForm.value)
    console.log(request);
    this.supportService.sendSupportRequest(request).then(resp=>{

  })
  }
  handleFileInput(files: FileList) {
    this.fileToUpload = files.item(0);

    if (this.fileToUpload)
      this.fileService.sendFile(this.fileToUpload)
        .then(resp => {
          this.fileList.push(resp.data.id)
        })
  }
}
