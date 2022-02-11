import {Component, HostListener, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NgbCarouselConfig, NgbTabset} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss'],
  providers: [NgbCarouselConfig]
})
export class MainPageComponent implements OnInit {
  currentTabId = '0';
  width:number = 0;
  activeAnimation = false;
  scrollValue = 0;
  constructor() { }

  ngOnInit(): void {
    this.width = document.body.clientWidth;
    // setInterval(() => {
    //   this.activeAnimation = this.isInViewport(document.getElementById('pay'));
    // }, 500);

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
  // isInViewport(elem: any): boolean {
  //   const bounding = elem.getBoundingClientRect();
  //   return (
  //     bounding.top >= 0 &&
  //     bounding.left >= 0 &&
  //     bounding.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
  //     bounding.right <= (window.innerWidth || document.documentElement.clientWidth)
  //   );
  // }
  log(event: any): void {
    this.scrollValue = event.target.scrollTop;
    console.log(event.target.scrollTop);
  }
  getImgSrc(str){
    const fileType = this.width<1200?'png':'svg';
    return str+fileType;
  }
}
