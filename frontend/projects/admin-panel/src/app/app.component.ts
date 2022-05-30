import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {AuthService} from './service/auth.service';
import {MassageModalComponent} from "../../../../common-blocks/massage-modal/massage-modal.component";
import {MassageServiceService} from "../../../../src/app/service/massage-service.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'adminPanel';
  subscription: Subscription = new Subscription();
  modalType: any;
  @ViewChild("massageModal", {static: false}) massageModal: MassageModalComponent;
  constructor(
    private authService: AuthService,
    private massageServiceService: MassageServiceService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.subscription.add(this.massageServiceService.missionAnnounced$.subscribe(massage=>{
      this.modalType = massage;
      this.massageModal.open();
    }));
    if (!this.authService.checkToken()) {
      // this.router.navigate(['/admin-panel/dashboard'])
      // } else {
      this.router.navigate([''])
    }

    this.subscription.add(
      this.authService.tokenStateChange.subscribe(
        (data: boolean) => {
          if (!data) {
            sessionStorage.removeItem('token')
            this.router.navigate([''])
          }
        }
      )
    )
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
