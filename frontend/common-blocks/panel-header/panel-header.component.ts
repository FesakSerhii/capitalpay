import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {ConfirmActionModalComponent} from '../confirm-action-modal/confirm-action-modal.component';
import {AuthService} from '../../src/app/service/auth.service';

@Component({
  selector: 'app-panel-header',
  templateUrl: './panel-header.component.html',
  styleUrls: ['./panel-header.component.scss']
})
export class PanelHeaderComponent implements OnInit {
  @Input() type: string;
  isExpanded = false;
  constructor(private router: Router, private authService:AuthService) { }
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
  accountDropdown: boolean = false;
  @ViewChild("confirmContent", {static: false}) confirmModal: ConfirmActionModalComponent;

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

  logOut() {
    this.confirmModal.open().then(resp=>{
      this.authService.logout();
      this.router.navigate([this.type==='admin'?'':'/page'], {});
    })
  }
  settings(){
    this.router.navigate([this.type==='admin'?'/admin-panel/settings':'/merchant/settings'], {});
  }
  profile(){
    this.router.navigate([this.type==='admin'?'':''], {});
  }
}
