import {Component, Input, OnInit} from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import {UserService} from '../../projects/admin-panel/src/app/service/user.service';
import {AuthService} from '../../src/app/service/auth.service';
import {Router} from '@angular/router';

const helper = new JwtHelperService();

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.scss']
})


export class SideMenuComponent implements OnInit {
  @Input() type: string;
  user: any;
  constructor(private userService:UserService,
              private router:Router,
              private authService:AuthService) { }
  menu = {
    merchant:[
      {
        route:'transaction-log',
        title:'История транзакций',
        icon:'sync_alt'
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
        route:'documents-layouts',
        title:'Оформление',
        icon:'description'
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
  body = window.document.getElementsByTagName('body')[0].className;
  isDuplicateSession:boolean=false;

  ngOnInit() {
    this.user = this.userService.getUserInfo();
    for(const role of this.user.authorities){
        this.userRoles.push(this.roleList[role])
    }
    setInterval(()=>{
      this.getSessions()
    },60000)
  }
  getSessions(){
    this.authService.checkSessions().then(resp=>{
      this.isDuplicateSession = resp;
    })
  }
  profile(){
    this.router.navigate([this.type==='admin'?'/admin-panel/settings':'/merchant/settings'], {queryParams:{info:true},
      queryParamsHandling: "merge"});
  }
}
