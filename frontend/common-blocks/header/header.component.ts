import { AfterViewInit, Component, Input, OnDestroy, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../src/app/service/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import {CheckFormInvalidService} from "../../src/app/service/check-form-invalid.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('login', { static: false }) loginModal: TemplateRef<any> | undefined;
  @ViewChild('register', { static: false }) registerModal: TemplateRef<any> | undefined;
  @Input() theme: string = 'white';
  constructor(
    public modalService: NgbModal,
    public authService: AuthService,
    private router: Router,
    public checkFormInvalidService:CheckFormInvalidService,
    private route: ActivatedRoute
  ) { }
  isDropdownOpen:boolean=false;
  width = 0;

  ngAfterViewInit(): void {
    this.width = document.body.clientWidth;
    this.subscription.add(
      this.route.queryParams.subscribe(
        (params) => {
          if (params?.login) {
            this.switchToLogin(this.loginModal)
          }
        }
      )
    )
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  subscription: Subscription = new Subscription();

  loginForm = new FormGroup({
    email: new FormControl(/*'+37096384345'*/undefined, Validators.required),
    // email: new FormControl('+37096384345', Validators.required),
    password: new FormControl(/*'blablabla'*/undefined, Validators.required),
    // password: new FormControl('blablabla', Validators.required),
    isIpTrusted: new FormControl(false)
  });
  registerForm = new FormGroup({
    username: new FormControl(undefined, Validators.required),
    email: new FormControl(undefined, Validators.required),
    comment: new FormControl(),
  })
  toggle = {
    showPass: false,
    showConfirmPass: false
  };
  isTwoFactor: boolean = false;
  sms = new FormControl();
  errStatusMassage: string = null;

  ngOnInit(): void {

  }
  open(modal: any) {
    this.modalService.open(modal)
  }

  switchToLogin(modal: any) {
    this.modalService.dismissAll();
    this.open(modal)
  }
  loginAction() {
    if(this.loginForm.invalid){
      this.errStatusMassage='Заполните все необходимые поля';
      return;
    }
    this.authService.login(this.loginForm.value.email, this.loginForm.value.password).then(resp => {
      if (resp.body['data'] === "SMS sent") {
        this.isTwoFactor = true;
      } else {
        sessionStorage.setItem('token', resp.headers.get('Authorization').replace('Bearer', '').trim());
        this.modalService.dismissAll();
        this.router.navigate(['/merchant/transaction-log']);
      }
    }).catch(err => {
      switch (err.status) {
        case 400: this.errStatusMassage = 'Ошибка регистрационных данных'; break;
        case 500: this.errStatusMassage = 'Ошибка сервера, попробуйте позже'; break;
        case 0: this.errStatusMassage = 'Отсутствие интернет соединения'; break;
        default: this.errStatusMassage = err.statusMessage; break;
      }
    })
  }

  confirmCode() {
    this.authService.login(this.loginForm.value.email, this.loginForm.value.password, this.sms.value).then(resp => {
      sessionStorage.setItem('token', resp.headers.get('Authorization').replace('Bearer', '').trim());
      this.modalService.dismissAll();
      this.router.navigate(['/merchant/transaction-log']);
    })
  }

  isInvalid(form: FormGroup|FormControl,field: string='') {
    return this.checkFormInvalidService.isInvalid(form,field);
  }
}
