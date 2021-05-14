import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {FormControl, FormGroup} from '@angular/forms';
import {StaticPageService} from '../../service/static-page.service';

@Component({
  selector: 'app-document-layouts-editor',
  templateUrl: './document-layouts-editor.component.html',
  styleUrls: ['./document-layouts-editor.component.scss']
})
export class DocumentLayoutsEditorComponent implements OnInit {

  constructor(private router: Router, private staticPageService:StaticPageService) { }
  activeTab = 'ru';
  ruForm = new FormGroup({
    "language": new FormControl('RUS'),
    "tag": new FormControl(),
    "name": new FormControl(),
    "content":new FormControl()
  })
  kzForm = new FormGroup({
    "language": new FormControl('KAZ'),
    "tag": new FormControl(),
    "name": new FormControl(),
    "content":new FormControl()
  })
  enForm = new FormGroup({
    "language": new FormControl('ENG'),
    "tag": new FormControl(),
    "name": new FormControl(),
    "content":new FormControl()
  })
  ngOnInit(): void {
  }

  navigate() {
    this.router.navigate(['/admin-panel/documents-layouts'])
  }

  save(form) {
    this.staticPageService.saveStaticPages(form.value).then(resp=>{
      console.log(resp);
      this.navigate();
    })
  }
}
