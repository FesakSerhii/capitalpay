import {Component, OnInit, ViewChild} from '@angular/core';
import {NgbTabset} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss'],
})
export class MainPageComponent implements OnInit {

  currentTabId: string = '0';
  constructor() { }
  // @ViewChild("tab", {static: false}) tab: NgbTabset;

  ngOnInit(): void {
    // this.tabset.select('1');
  }
  nextTab(){
    // this.tab.select(String(+this.currentTabId+1));
    this.currentTabId = String(+this.currentTabId+1);
  }
  prevTab(){
    // this.tab.select(String(+this.currentTabId-1));
    this.currentTabId = String(+this.currentTabId-1);
  }
}
