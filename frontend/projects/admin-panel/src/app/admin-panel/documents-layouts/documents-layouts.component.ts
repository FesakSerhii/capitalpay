import {Component, OnInit, ViewChild} from '@angular/core';
import {StaticPageService} from '../../service/static-page.service';
import {Router} from '@angular/router';
import {ConfirmActionModalComponent} from '../../../../../../common-blocks/confirm-action-modal/confirm-action-modal.component';

@Component({
  selector: 'app-documents-layouts',
  templateUrl: './documents-layouts.component.html',
  styleUrls: ['./documents-layouts.component.scss']
})
export class DocumentsLayoutsComponent implements OnInit {
  activePage: any = null;
  constructor(private router: Router,private staticPageService:StaticPageService) { }
  @ViewChild("confirmContent", {static: false}) confirmModal: ConfirmActionModalComponent;

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
  navigateToEditor(tag=null){
    this.router.navigate(['/admin-panel/documents-layouts/editor'],{queryParams: {
        tag: tag,
      }})
  }

  delete(tag) {
    this.activePage = null;
    this.confirmModal.open().then(resp=> {
      this.staticPageService.deleteStaticPages(tag).then(()=>{
        this.getPages()
      })
    })

  }
}
