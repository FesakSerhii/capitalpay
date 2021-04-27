import {Component, Input, OnInit} from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import {UserService} from "../../projects/admin-panel/src/app/service/user.service";

const helper = new JwtHelperService();

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.scss']
})


export class SideMenuComponent implements OnInit {
  @Input() type: string;
  user: any;
  constructor(private userService:UserService) { }
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
    ]
  };
  roleList = {
    ROLE_USER:'Пользователь',
    ROLE_OPERATOR:'Оператор',
    ROLE_ADMIN:'Админ',
    ROLE_MERCHANT:'Мерчант'
  };
  userRoles = [];

  ngOnInit() {
    this.user = this.userService.getUserInfo();
    for(let role of this.user.authorities){
        this.userRoles.push(this.roleList[role])
    }
    this.userRoles.join('/')
  }

}
