import {Component, HostListener, Inject, OnDestroy, OnInit} from '@angular/core';
import {DOCUMENT} from '@angular/common';
import {NgbCarouselConfig} from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { AuthService } from './service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [NgbCarouselConfig]
})
export class AppComponent implements OnInit, OnDestroy {
  subscription: Subscription = new Subscription();
  title = 'capitalPay';

  constructor(
    config: NgbCarouselConfig,
    private authService: AuthService,
    private router: Router
    ) {
    config.interval = 2000;
    config.keyboard = true;
    config.pauseOnHover = true;
  }

  ngOnInit() {
    if (!this.authService.checkToken()) {
    //   this.router.navigate(['/merchant/transaction-log'])
    // } else {
      this.router.navigate(['page'])
    }

    this.subscription.add(
      this.authService.tokenStateChange.subscribe(
        (data: boolean) => {
          if (!data) {
            sessionStorage.removeItem('token')
            this.router.navigate(['page'], { queryParams: { login: true } });
          }
        }
      )
    )
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
