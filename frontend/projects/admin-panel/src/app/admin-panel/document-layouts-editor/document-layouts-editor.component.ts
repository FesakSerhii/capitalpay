import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl, FormGroup} from '@angular/forms';
import {StaticPageService} from '../../service/static-page.service';
import * as ClassicEditor from '@ckeditor/ckeditor5-build-classic';

@Component({
  selector: 'app-document-layouts-editor',
  templateUrl: './document-layouts-editor.component.html',
  styleUrls: ['./document-layouts-editor.component.scss']
})
export class DocumentLayoutsEditorComponent implements OnInit {

  constructor( private activatedRoute: ActivatedRoute,
               private router: Router,
               private staticPageService:StaticPageService) { }
  public Editor = ClassicEditor;
  activeTab = 'RUS';
  RUSForm = new FormGroup({
    "language": new FormControl('RUS'),
    "tag": new FormControl(),
    "name": new FormControl(),
    "content":new FormControl()
  })
  KAZForm = new FormGroup({
    "language": new FormControl('KAZ'),
    "tag": new FormControl(),
    "name": new FormControl(),
    "content":new FormControl()
  })
  ENGForm = new FormGroup({
    "language": new FormControl('ENG'),
    "tag": new FormControl(),
    "name": new FormControl(),
    "content":new FormControl()
  })
  activeTag:string=null
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

  save(form) {
    this.staticPageService.saveStaticPages(form.value).then(resp=>{
      this.navigate();
    })
  }
}
