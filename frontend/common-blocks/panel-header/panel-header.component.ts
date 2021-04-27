import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-panel-header',
  templateUrl: './panel-header.component.html',
  styleUrls: ['./panel-header.component.scss']
})
export class PanelHeaderComponent implements OnInit {
  isExpanded = false;
  constructor(private router: Router) { }
  pageTitles ={
    "dashboard":'Дашборд',
    "transaction-log":'История транзакций',
    "settings":'Настройки',
    "help":'Тех поддержка',
    "currencies":'Валюты',
    "payment-methods":'Платежные методы',
    "user":'Пользователи',
    "user/settings":'Настройки пользователя'
  };
  route = null;

  ngOnInit(): void {
    this.route = this.router.url.replace('/admin-panel/','').trim();
  }
  toogleSideMenu() {
    if (this.isExpanded) {
      window.document.getElementsByTagName('body')[0].className = '';
      this.isExpanded = false;
    } else {
      window.document.getElementsByTagName('body')[0].className = 'sidebar-mini';
      this.isExpanded = true;
    }
  }
}
