import {Component, OnInit} from '@angular/core';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {RecoverPasswordModalComponent} from '../recover-password-modal/recover-password-modal.component';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../../src/app/service/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login-modal',
  templateUrl: './login-modal.component.html',
  styleUrls: ['./login-modal.component.scss']
})
export class LoginModalComponent implements OnInit {

  config = {
    class: 'my-modal'
  };

  loginForm: FormGroup
  isVisible = false
  inputPasswordType = 'password'

  // This variable is copied from header component
  isTwoFactor = false;
  errStatusMassage: string = null;

  constructor(public activeModal: NgbActiveModal,
              public modalService: NgbModal,
              public authService: AuthService,
              private router: Router,) {
  }

  ngOnInit(): void {
    this.formInit()
  }

  formInit() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.required]),
      password: new FormControl('', Validators.required),
    })
  }

  loginUser() {
    // This method is copied from header component
    this.authService.login(`+${this.loginForm.value.email}`, this.loginForm.value.password).then(resp => {
      if (resp.body['data'] === "SMS sent") {
        this.isTwoFactor = true;
      } else {
        sessionStorage.setItem('token', resp.headers.get('Authorization').replace('Bearer', '').trim());
        this.modalService.dismissAll();
        this.router.navigate(['/merchant/transaction-log']);
      }
    }).catch(err => {
      switch (err.status) {
        case 400:
          this.errStatusMassage = 'Ошибка регистрационных данных';
          break;
        case 500:
          this.errStatusMassage = 'Ошибка сервера, попробуйте позже';
          break;
        case 0:
          this.errStatusMassage = 'Отсутствие интернет соединения';
          break;
        default:
          this.errStatusMassage = err.statusMessage;
          break;
      }
    })
  }

  visibilityPassword() {
    if (!this.isVisible) {
      this.inputPasswordType = 'text'
      this.isVisible = true
    } else {
      this.inputPasswordType = 'password'
      this.isVisible = false
    }
  }

  recoveryPassword() {
    this.activeModal.close()
    this.modalService.open(RecoverPasswordModalComponent, { windowClass: 'lending-modal' })
  }
}
