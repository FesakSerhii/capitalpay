import { Component, OnInit } from '@angular/core';
import {StaticPageService} from '../../service/static-page.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-documents-layouts',
  templateUrl: './documents-layouts.component.html',
  styleUrls: ['./documents-layouts.component.scss']
})
export class DocumentsLayoutsComponent implements OnInit {
  isActive: boolean =false;
  constructor(private router: Router,private staticPageService:StaticPageService) { }
  activeTab = 'tab1';
  pagesList: [any] = null;
  ngOnInit(): void {
    this.getPages();
  }
  getPages(){
    this.staticPageService.getStaticPages().then(resp=>{
      this.pagesList = resp.data;
    })
  }
  navigateToEditor(){
    this.router.navigate(['/admin-panel/documents-layouts/editor'])
  }

  delete() {
    this.staticPageService.deleteStaticPages('').then(()=>{
      this.getPages()
    })
  }
}
