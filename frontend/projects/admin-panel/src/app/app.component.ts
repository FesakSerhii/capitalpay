import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from './service/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'adminPanel';
  subscription: Subscription = new Subscription();

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
  }

  ngOnInit() {
    if (this.authService.checkToken()) {
      this.router.navigate(['/admin-panel/dashboard'])
    } else {
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
}
