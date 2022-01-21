import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RedirectService {

  constructor() {
  }

  redirectTimerValue: number = 5
  limitedInterval: any = null;

  private redirect = new BehaviorSubject(false);
  redirectEvent = this.redirect.asObservable();

  private redirectTimer = new BehaviorSubject(this.redirectTimerValue);
  redirectTimerEvent = this.redirectTimer.asObservable();


  redirectTimerStart() {
    console.log('start interval in service:',new Date());
    this.limitedInterval = setInterval(() => {
      console.log('inside interval:',this.redirectTimerValue);
      if (this.redirectTimerValue) {
        this.redirectTimerValue = this.redirectTimerValue - 1;
        this.redirectTimer.next(this.redirectTimerValue)
      } else {
        clearInterval(this.limitedInterval);
        this.redirect.next(true)
      }
    }, 1000)
  }
}
