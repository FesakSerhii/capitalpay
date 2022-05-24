import {Component, HostListener, Inject, Input, OnDestroy, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {DOCUMENT} from '@angular/common';
import {NgbCarouselConfig} from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { AuthService } from './service/auth.service';
import { Router } from '@angular/router';
import {MassageServiceService} from "./service/massage-service.service";
import {MassageModalComponent} from "../../common-blocks/massage-modal/massage-modal.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [NgbCarouselConfig]
})
export class AppComponent implements OnInit, OnDestroy {
  @ViewChild("massageModal", {static: false}) massageModal: MassageModalComponent;
  subscription: Subscription = new Subscription();
  title = 'capitalPay';
  modalType: any;

  constructor(
    config: NgbCarouselConfig,
    private authService: AuthService,
    private massageServiceService: MassageServiceService,
    private router: Router
    ) {
    config.interval = 2000;
    config.keyboard = true;
    config.pauseOnHover = true;
  }

  ngOnInit() {
    this.subscription.add(this.massageServiceService.missionAnnounced$.subscribe(massage=>{
      this.modalType = massage;
      this.massageModal.open();
    }));
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
