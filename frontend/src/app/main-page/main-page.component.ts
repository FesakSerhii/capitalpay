import {Component, HostListener, NgModule, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NgbCarouselConfig, NgbTabset} from '@ng-bootstrap/ng-bootstrap';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {validate} from "codelyzer/walkerFactory/walkerFn";
import {SupportService} from "../service/support.service";


@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss'],
  providers: [NgbCarouselConfig]
})
export class MainPageComponent implements OnInit {
  currentTabId = '0';
  width: number = 0;
  activeAnimation = false;
  scrollValue = 0;
  mainForm: FormGroup
  successStatusMassage: any = null
  constructor(private supportService: SupportService) {
  }

  ngOnInit(): void {
    this.width = document.body.clientWidth;
    // setInterval(() => {
    //   this.activeAnimation = this.isInViewport(document.getElementById('pay'));
    // }, 500);

    // this.tabset.select('1');

    this.formInit()
  }

  formInit() {
    this.mainForm = new FormGroup({
      phone: new FormControl('', Validators.required),
      name: new FormControl('', Validators.required),
      email: new FormControl('', [Validators.required, Validators.email]),
      comment: new FormControl('', Validators.required),
    })
  }

  nextTab(): void {
    // this.tab.select(String(+this.currentTabId+1));
    this.currentTabId = String(+this.currentTabId + 1);
  }

  prevTab(): void {
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
  slideConfig = {"slidesToShow": 2, "slidesToScroll": 3};
  slides = [
    {img: this.getImgSrc('../../assets/shapes/carousel/3ds.')},
    {img: this.getImgSrc('../../assets/shapes/carousel/comodo.')},
    {img: this.getImgSrc('../../assets/shapes/carousel/pcj.')},
    {img: this.getImgSrc('../../assets/shapes/carousel/visa.')},
    {img: this.getImgSrc('../../assets/shapes/carousel/mastercard.')},
    {img: this.getImgSrc('../../assets/shapes/carousel/qiwi.')},
    {img: this.getImgSrc('../../assets/shapes/carousel/am-ex.')},
    {img: this.getImgSrc('../../assets/shapes/carousel/alfa.')},
  ];

  log(event: any): void {
    this.scrollValue = event.target.scrollTop;
    console.log(event.target.scrollTop);
  }

  getImgSrc(str) {
    const fileType = this.width < 1200 ? 'png' : 'svg';
    return str + fileType;
  }

  getSlickConfig() {
    if (this.width <= 320 || (this.width <= 769 && this.width > 321)) {
      return {slidesToShow: 2, slidesToScroll: 1, infinite: true, autoplay: true}
    } else if (this.width >= 769) {
      return {slidesToShow: 5, slidesToScroll: 1, infinite: true, dots: true, autoplay: true}
    }
  }

  sendForm(): void {
    const formData = {...this.mainForm.value};
    this.supportService.sendFeedback(formData).then(resp => {
      if(resp.result === true) {
        this.mainForm.reset()
        this.successStatusMassage = 'Заявка успешно отправлена'
        setTimeout(() => {
          this.successStatusMassage = null;
        }, 5000)
      }
    })
  }
}
