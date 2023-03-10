import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, PatternValidator, Validators} from '@angular/forms';
import {RegisterService} from '../service/register.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CheckFormInvalidService} from "../service/check-form-invalid.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  constructor(public registerService: RegisterService, public checkFormInvalidService:CheckFormInvalidService, public activatedRoute: ActivatedRoute, private router: Router) { }
  phoneNumber = new FormControl('+45566678799');
  resendTimer: any = null;
  timerValue = 30;
  step = 0;
  code = null;
  toggle = {
    showPass : false,
    showConfirmPass: false
  };
  email = new FormControl();
  errStatusMassage: string = null;
  errRegisterMassage: boolean = false;
  errCodeMassage: boolean = false;

  // tslint:disable-next-line:typedef
  password = new FormControl('', [Validators.pattern('^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$')]);
  passwordConfirm = new FormControl();
  confirmCodeForm = new FormControl();

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe((param) => {
      console.log(param);
      // @ts-ignore
      this.code = param.get('code');
      if (this.code !== null){
        // @ts-ignore
        this.registerService.confirmEmail(this.code).then(resp => {
          this.step = 3;
        });
      }

    });
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
  sendEmail(): void {
    if(this.email.invalid){
      this.errRegisterMassage=true;
      return;
    }
    this.registerService.postEmail(this.email.value).then(resp => {
      this.step = ++this.step;
    }).catch(err => {
      switch (err.status) {
        case 500: this.errStatusMassage = '???????????? ??????????????, ???????????????????? ??????????'; break;
        case 0: this.errStatusMassage = '???????????????????? ???????????????? ????????????????????'; break;
        default: this.errStatusMassage = err.statusMessage; break;
      }
    });
  }
  sendCode(): void {
    this.step = ++this.step;
    this.startTimer();
  }
  comparePasswords(){
    return (this.password.value&&this.passwordConfirm.value)&&(this.password.value===this.passwordConfirm.value)
  }
  confirmCode(): void {
    if(this.confirmCodeForm.invalid){
      this.errCodeMassage=true;
      return;
    }
    this.registerService.confirmPhone(this.confirmCodeForm.value).then(resp => {
      this.resendTimer.clearInterval();
      this.timerValue = 30;
      this.navigate();
    }).catch(err => {
      switch (err.status) {
        case 500: this.errStatusMassage = '???????????? ??????????????, ???????????????????? ??????????'; break;
        case 0: this.errStatusMassage = '???????????????????? ???????????????? ????????????????????'; break;
        default: this.errStatusMassage = err.statusMessage; break;
      }
    });
  }

  navigate(): void {
    this.router.navigate(['']);
  }

  sendPhoneAndPassword(): void {
    if(this.phoneNumber.invalid){
      this.errStatusMassage='Fill all required fields';
      return;
    }
    if (this.password.invalid){
      this.password.reset();
      this.passwordConfirm.reset();
    }else if (this.password.value !== this.passwordConfirm.value){
      this.passwordConfirm.reset();
    }else{
      // @ts-ignore
      this.registerService.sendPassword(this.code, this.password.value, this.phoneNumber.value).then(resp => {
        this.step = ++this.step;
        this.startTimer();
      }).catch(err => {
        switch (err.status) {
          case 500: this.errStatusMassage = '???????????? ??????????????, ???????????????????? ??????????'; break;
          case 0: this.errStatusMassage = '???????????????????? ???????????????? ????????????????????'; break;
          default: this.errStatusMassage = err.statusMessage; break;
        }
      });
    }
  }

  isInvalid(form: FormGroup|FormControl,field: string='') {
    return this.checkFormInvalidService.isInvalid(form,field);
  }
}
