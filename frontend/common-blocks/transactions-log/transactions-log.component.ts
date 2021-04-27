import { Component, OnInit } from '@angular/core';
import {UserService} from "../../projects/admin-panel/src/app/service/user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-transactions-log',
  templateUrl: './transactions-log.component.html',
  styleUrls: ['./transactions-log.component.scss']
})
export class TransactionsLogComponent implements OnInit {

  constructor(private userService:UserService,private router:Router) { }
  activeTab = 'tab1';
  user:any;
  idAdmin:boolean;
  ngOnInit(): void {
    this.user = this.userService.getUserInfo();
    this.idAdmin = this.router.url.includes('admin-panel');
  }

}
