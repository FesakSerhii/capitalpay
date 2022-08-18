import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {P2pService} from "../../service/p2p.service";
import {repeat, timeInterval, switchMap, takeUntil, mergeMap, filter, take, catchError} from 'rxjs/operators';
import {interval, pipe, ReplaySubject, timer} from 'rxjs';


@Component({
  selector: 'app-card-check',
  templateUrl: './card-check.component.html',
  styleUrls: ['./card-check.component.scss']
})
export class CardCheckComponent implements OnInit, OnDestroy {
  isError: boolean = true;
  userId: string;
  orderId: string;
  counter: number;
  sub$ = new ReplaySubject();
  // http://localhost:5200/admin-panel/card-check?userId=663&orderId=00000164372337

  constructor(private activatedRoute: ActivatedRoute, private p2pService: P2pService, private router: Router) {
  }

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.pipe(takeUntil(this.sub$)).subscribe((param) => {
      this.userId = param.get('userId');
      this.orderId = param.get('orderId');
      if (this.userId && this.orderId) {
        this.isError = false;
        interval(30000).pipe(
          switchMap(() => this.p2pService.bankCheckValidity(this.orderId)),
          filter((res) => res.result),
          take(1),
          takeUntil(this.sub$))
          .subscribe(value => {
            this.returnToSettings();
          })
      } else {
        this.errHandle(null);
      }
    })
  }
  errHandle(err) {
    this.counter = 5;
    timer(0, 1000).pipe(
      take(5)
    ).subscribe({
      next: (i) => this.counter = 5 - i,
      complete: () => this.returnToSettings(),
    })
  }

  returnToSettings() {
    this.router.navigate(['admin-panel/user/settings'], {
      queryParams: {
        userId: this.userId,
      },
      queryParamsHandling: 'merge'
    })
  }

  ngOnDestroy(): void {
    this.sub$.next();
    this.sub$.complete();
  }
}
