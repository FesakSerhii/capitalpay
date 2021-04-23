import {Component, Input, OnInit} from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

const helper = new JwtHelperService();

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.scss']
})


export class SideMenuComponent implements OnInit {
  @Input() type: string;
  constructor() { }
  user:any = {};
  menu = {
    merchant:[
      {
        route:'transactions-log',
        title:'История транзакций',
        icon:'sync alt'
      },
      {
        route:'settings',
        title:'Настройки',
        icon:'settings'
      },
      {
        route:'tools',
        title:'Инструменты',
        icon:'widgets'
      },
      {
        route:'support',
        title:'Тех поддержка',
        icon:'contact_support'
      },
      {
        route:'api',
        title:'API',
        icon:'api'
      }
    ],
    admin:[
      {
        route:'dashboard',
        title:'Дашборд',
        icon:'dashboard '
      },
      {
        route:'transaction-log',
        title:'История транзакций',
        icon:'sync_alt '
      },
      {
        route:'settings',
        title:'Настройки',
        icon:'settings  '
      },
      {
        route:'user',
        title:'Пользователи',
        icon:'person  '
      },
      {
        route:'help',
        title:'Тех поддержка',
        icon:'question_answer'
      },
      {
        route:'currencies',
        title:'Валюты',
        icon:'monetization_on'
      },
      {
        route:'payment-methods',
        title:'Платежные методы',
        icon:'checklist'
      }
    ]
  };

  ngOnInit() {
    if(sessionStorage.getItem('id_token')){
      this.user = helper.decodeToken(sessionStorage.getItem('id_token'))
    }else{
      this.user.name = 'Тут кароч ФИО будет'
    }

  }

}
