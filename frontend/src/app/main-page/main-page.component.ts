import {Component, HostListener, OnInit, ViewChild} from '@angular/core';
import {NgbTabset} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss'],
})
export class MainPageComponent implements OnInit {
  currentTabId = '0';
  width = 0;
  constructor() { }
  // @ViewChild("tab", {static: false}) tab: NgbTabset;

  ngOnInit(): void {
    this.width = document.body.clientWidth;
    // this.tabset.select('1');
  }
  nextTab(): void{
    // this.tab.select(String(+this.currentTabId+1));
    this.currentTabId = String(+this.currentTabId + 1);
  }
  prevTab(): void{
    // this.tab.select(String(+this.currentTabId-1));
    this.currentTabId = String(+this.currentTabId - 1);
  }
}
