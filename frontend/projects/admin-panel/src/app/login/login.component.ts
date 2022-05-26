import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import { HttpResponse } from "@angular/common/http";
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {CheckFormInvalidService} from "../../../../../src/app/service/check-form-invalid.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  constructor(
    public authService: AuthService,
    public checkFormInvalidService:CheckFormInvalidService,
    public modalService: NgbModal,
    private router: Router
  ) { }

  loginForm = new FormGroup({
    // email: new FormControl(/*'+37096384345'*/undefined, Validators.required),
    email: new FormControl('+37096384345', Validators.required),
    // password: new FormControl(/*'blablabla'*/undefined, Validators.required),
    password: new FormControl('blablabla', Validators.required),
    isIpTrusted: new FormControl()
  });

  toggle = {
    showPass: false,
    showConfirmPass: false
  };

  isTwoFactor: boolean = false;
  sms = new FormControl();
  errStatusMassage: string = null;

  ngOnInit(): void {
  }

  loginAction() {
    if(this.loginForm.invalid){
      this.errStatusMassage='Заполните все необходимые поля';
      return;
    }
    this.authService.login(this.loginForm.value.email, this.loginForm.value.password).then((resp: HttpResponse<any>) => {
      console.log(resp);
      if (resp.body.data === "SMS sent") {
        this.isTwoFactor = true;
      } else {
        sessionStorage.setItem('token', resp.headers.get('Authorization').replace('Bearer', '').trim());
        this.router.navigate(['/admin-panel/dashboard']);
      }
    }).catch(err => {
      switch (err.status) {
        case 400: this.errStatusMassage = 'Ошибка регистрационных данных'; break;
        case 500: this.errStatusMassage = 'Ошибка сервера, попробуйте позже'; break;
        case 0: this.errStatusMassage = 'Отсутствие интернет соединения'; break;
        default: this.errStatusMassage = err.statusMessage; break;
      }
    } )
  }

  confirmCode() {
    this.authService.login(this.loginForm.value.email, this.loginForm.value.password, this.sms.value).then(resp => {
      sessionStorage.setItem('token', resp.headers.get('Authorization').replace('Bearer', '').trim());
      this.router.navigate(['/admin-panel/dashboard']);
    })
  }
  isInvalid(form: FormGroup|FormControl,field: string='') {
    return this.checkFormInvalidService.isInvalid(form,field);
  }
}
