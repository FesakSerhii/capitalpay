import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-api',
  templateUrl: './api.component.html',
  styleUrls: ['./api.component.scss']
})
export class ApiComponent implements OnInit {
  apiTitles=[
    {title:'Валюты',id:0},
    {title:'Платежные системы',id:1},
    {title:'Кассы',id:2},
    {title:'Статические страницы',id:3},
    {title:'Платежи мерчанта',id:4},
  ];
  sections=[
    {titles:[
        {title:'Валюты',id:0},
        {title:'Платежные системы',id:1},
        {title:'Кассы',id:2},
        {title:'Статические страницы',id:3},
        {title:'Платежи мерчанта',id:4},
      ],id:0,active:false},
  ];

  constructor() { }

  ngOnInit(): void {
  }
  openApiSection(id){
    this.sections[id].active=!this.sections[id].active
  }
}
