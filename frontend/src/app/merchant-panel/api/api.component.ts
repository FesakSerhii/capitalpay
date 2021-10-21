import { Component, OnInit } from '@angular/core';
import {StaticPageService} from '../../../../projects/admin-panel/src/app/service/static-page.service';

@Component({
  selector: 'app-api',
  templateUrl: './api.component.html',
  styleUrls: ['./api.component.scss']
})
export class ApiComponent implements OnInit {
  apiTitles=[];
  tags=['currency','paysystem','cashbox']
  apiSections=[]
  container:any = null;
  activeSectionId:number = null;
  constructor(private staticPageService:StaticPageService) { }

  ngOnInit(): void {
    const promises = []
    for(let tag of this.tags){
      const reqObj = {
        "language": 'RUS',
        "tag": tag
      }
      promises.push(this.staticPageService.getStaticPage(reqObj));
    }
    Promise.all(promises).then(resp=>{
      this.apiSections = resp.map(el=>{
        return el.data
      })
      this.apiTitles = resp.map(el=>{
       return el.data.name
      })
    });
  }
  openSection(item) {
    console.log(this.activeSectionId !== item.id);
    if(document.getElementById('content').childElementCount){
      document.getElementById('content').innerHTML = ""
    }else{
      this.activeSectionId = item.id;
      document.getElementById('content').insertAdjacentHTML('afterbegin',item.content)
    }
  }

  changeSectionContent(item) {
    document.getElementById('content').innerHTML = ""
    this.activeSectionId = item.id;
    document.getElementById('content').insertAdjacentHTML('afterbegin',item.content)
  }
}
