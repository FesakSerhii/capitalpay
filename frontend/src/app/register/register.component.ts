import { Component, OnInit } from '@angular/core';
import {FormControl} from '@angular/forms';
import {RegisterService} from '../service/register.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  constructor(public registerService: RegisterService) { }
  phoneNumber = new FormControl('+ 45 (566) 678 79 99');
  resendTimer: any = null;
  timerValue = 30;
  step = 0;
  toggle = {
    showPass : false,
    showConfirmPass: false
  };
  email = new FormControl();

  ngOnInit(): void {
    console.log('fff');
  }
  startTimer(): void {
    this.resendTimer = setInterval(() => {
      if (this.timerValue > 0){
        this.timerValue = --this.timerValue;
      }else{
        this.resendTimer.clearInterval();
      }
    }, 1000);
  }

  // tslint:disable-next-line:typedef
  sendEmail() {
    this.registerService.postEmail(this.email.value).then(resp => {
      this.step = ++this.step;
    });
  }
  sendCode(): void {
    this.step = ++this.step;
    this.startTimer();
  }

  confirmCode(): void {
    this.step = ++this.step;
    this.resendTimer.clearInterval();
    this.timerValue = 30;
  }
}
