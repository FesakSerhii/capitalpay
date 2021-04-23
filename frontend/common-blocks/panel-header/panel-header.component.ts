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

  ngOnInit(): void {
    console.log(this.router.url);
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
