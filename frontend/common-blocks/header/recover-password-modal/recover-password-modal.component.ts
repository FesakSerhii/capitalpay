import { Component, OnInit } from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-recover-password-modal',
  templateUrl: './recover-password-modal.component.html',
  styleUrls: ['./recover-password-modal.component.scss']
})
export class RecoverPasswordModalComponent implements OnInit {

  recoveryForm: FormGroup

  constructor(public activeModal: NgbActiveModal,) { }

  ngOnInit(): void {
    this.formInit()
  }

  formInit() {
    this.recoveryForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
    })
  }

  sendRequestRecoveryPassword() {
    this.activeModal.close()
    alert('Тут должен быть метод отправки запроса на восстановления пароля')
  }
}
