import { Component, OnInit } from '@angular/core';
import {StaticPageService} from '../../../../projects/admin-panel/src/app/service/static-page.service';

@Component({
  selector: 'app-api',
  templateUrl: './api.component.html',
  styleUrls: ['./api.component.scss']
})
export class ApiComponent implements OnInit {
  apiTitles=[
    {title:'Валюты',id:0,sections:[],tags:['currency/system/list','currency/system/edit','currency/system/add','currency/merchant/list','currency/cashbox/list','currency/cashbox/edit']},
    {title:'Платежные системы',id:1,sections:[],tags:['paysystem/system/list','paysystem/system/enable','paysystem/merchant/list','paysystem/merchant/edit','paysystem/cashbox/list','paysystem/cashbox/edit','интернет-магазин']},
    {title:'Кассы',id:2,sections:[],tags:['cashbox/create','cashbox/changename','cashboxsetting/get','cashboxsetting/set','cashbox/delete','cashbox/all']},
    {title:'Платежи мерчанта',id:3,sections:[],tags:['payments/list','payments/one','paysystems/halyk/get','paysystems/halyk/set','paysystems/register/download']}
  ];
  tags=['currency','paysystem','cashbox']
  apiSections=[]
  container:any = null;
  activeSectionId:number = null;
  activeTitleId:number = null;
  constructor(private staticPageService:StaticPageService) { }

  ngOnInit(): void {
    this.getSections()
  }
  openSection(item) {
    if(document.getElementById('content').childElementCount){
      document.getElementById('content').innerHTML = ""
      this.activeSectionId = null;
    }else{
      this.activeSectionId = item.id;
      document.getElementById('content').insertAdjacentHTML('afterbegin',item.content)
    }
  }
  getSections(){
    for(let section of this.apiTitles){
      const promises = []
      for(let tag of section.tags){
        const reqObj = {
          "language": 'RUS',
          "tag": tag
        }
        promises.push(this.staticPageService.getStaticPage(reqObj));
      }
      Promise.all(promises).then(resp=>{
        section.sections = resp.map(el=>{
          return el.data
        })
      });
    }
  }

  changeSectionContent(item) {
    document.getElementById('content').innerHTML = ""
    this.activeSectionId = item.id;
    document.getElementById('content').insertAdjacentHTML('afterbegin',item.content)
  }
}
