import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {StaticPageService} from '../../service/static-page.service';
import * as ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import {CheckFormInvalidService} from "../../../../../../src/app/service/check-form-invalid.service";

@Component({
  selector: 'app-document-layouts-editor',
  templateUrl: './document-layouts-editor.component.html',
  styleUrls: ['./document-layouts-editor.component.scss']
})
export class DocumentLayoutsEditorComponent implements OnInit {

  constructor( private activatedRoute: ActivatedRoute,
               private router: Router,
               public checkFormInvalidService:CheckFormInvalidService,
               private staticPageService:StaticPageService) { }
  public Editor = ClassicEditor;
  activeTab = 'RUS';
  RUSForm = new FormGroup({
    "language": new FormControl('RUS'),
    "tag": new FormControl(undefined, Validators.required),
    "name": new FormControl(undefined, Validators.required),
    "content":new FormControl(undefined, Validators.required)
  })
  KAZForm = new FormGroup({
    "language": new FormControl('KAZ'),
    "tag": new FormControl(undefined, Validators.required),
    "name": new FormControl(undefined, Validators.required),
    "content":new FormControl(undefined, Validators.required)
  })
  ENGForm = new FormGroup({
    "language": new FormControl('ENG'),
    "tag": new FormControl(undefined,[Validators.required]),
    "name": new FormControl(undefined,[Validators.required]),
    "content":new FormControl(undefined,[Validators.required])
  })
  activeTag:string=null
  RUSFormErrStatusMassage: string = null;
  KAZFormErrStatusMassage: string = null;
  ENGFormErrStatusMassage: string = null;
  errStatusMassage: string = null;
  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe((param) => {
      this.activeTag = param.get('tag');
      if(this.activeTag){
        this.getPage('RUS')
      }
    });
  }
  getPage(lang){
    const reqObj = {
      "language": lang,
      "tag": this.activeTag
    }
    this.staticPageService.getStaticPage(reqObj).then(resp=>{
      this[lang+'Form'].patchValue(resp.data)
    })
  }
  navigate() {
    this.router.navigate(['/admin-panel/documents-layouts'])
  }

  save(form,formName){
    if(form.invalid){
      this[formName+'ErrStatusMassage'] = 'Заполните все необходимые поля';
      return;
    }
    this.staticPageService.saveStaticPages(form.value).then(resp=>{
      this.navigate();
    }).catch(err => {
      switch (err.status) {
        case 500: this[formName+'ErrStatusMassage'] = 'Ошибка сервера, попробуйте позже'; break;
        case 0: this[formName+'ErrStatusMassage'] = 'Отсутствие интернет соединения'; break;
        default: this[formName+'ErrStatusMassage'] = err.statusMessage; break;
      }
    })
  }
  isInvalid(form: FormGroup|FormControl,field: string='') {
    return this.checkFormInvalidService.isInvalid(form,field);
  }
}
