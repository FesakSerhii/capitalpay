import {Component, HostListener, Inject} from '@angular/core';
import {DOCUMENT} from '@angular/common';
import {NgbCarouselConfig} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [NgbCarouselConfig]
})
export class AppComponent {
  title = 'capitalPay';
  constructor(config: NgbCarouselConfig) {
    //
    config.interval = 2000;
    config.keyboard = true;
    config.pauseOnHover = true;
  }
}
