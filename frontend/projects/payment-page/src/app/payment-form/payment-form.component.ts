import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {UserCardService} from '../service/user-card.service';
import {ActivatedRoute} from '@angular/router';
import {validateCard} from '../../../../../common-blocks/validators/paymentCardNumberValidator';
import {expirationDateValidator} from '../../../../../common-blocks/validators/dateCompareValidator';
import {filter} from 'rxjs/operators';
import {BankErrorCodesService} from '../service/bank-error-codes.service';
import {RedirectService} from '../service/redirect.service';

@Component({
  selector: 'app-payment-form',
  templateUrl: './payment-form.component.html',
  styleUrls: ['./payment-form.component.scss']
})
export class PaymentFormComponent implements OnInit {

  @Output() cardDataEvent = new EventEmitter();
  @Output() cancelEvent = new EventEmitter();
  @Input() validityErrorCode: string = null;
  @Input() validityError: boolean = false;
  @Input() queryParamEnable: boolean = true;
  @Input() p2pEnabled: boolean = true;
  @Input() redirect: boolean = false;

  cardForm = new FormGroup({
    cardNumber: new FormControl('', [validateCard()]),
    cardHolderName: new FormControl(),
    expirationMonth: new FormControl(),
    expirationYear: new FormControl(),
    phone: new FormControl(),
    email: new FormControl(),
    cvv2Code: new FormControl('',[Validators.min(100),Validators.max(999)]),
  });
  errStatusMassage: string = null;
  token: string = null;
  id: number = null;
  merchantId: number = null;
  cashBoxId: number = null;
  cashBoxInfo: any = null;
  limitedInterval: any = null;
  // isCardValid: boolean = false;
  expInvalid: boolean = false;

  // validityError: boolean = false;
  // validityErrorCode: boolean = false;
  redirectTimer: number = 5;
  // parameters: string = null;
  acceptTerms = new FormControl();
  yearsArr: number[] = [];

  constructor(private userCardService: UserCardService,
              private activatedRoute: ActivatedRoute,
              public bankErrorCodesService:BankErrorCodesService,
              private redirectService: RedirectService) {
  }

  ngOnInit(): void {
    let currentYear = new Date().getFullYear();
    for (let i = currentYear; i < currentYear + 6; i++) {
      this.yearsArr.push(currentYear);
      currentYear++
    }
    this.cardHolderNameEnabled()
    let dateFields = {
      expirationMonth: 'expirationYear',
    };
    for (let v in dateFields) {
      this.cardForm.get(v).setValidators([Validators.required, expirationDateValidator(this.cardForm, dateFields[v])]);
      this.cardForm.get(dateFields[v]).setValidators([Validators.required, expirationDateValidator(this.cardForm, v, true)]);
    }
    this.redirectService.redirectTimerEvent.subscribe(value => {
      console.log(value);
      this.redirectTimer = value;
      if(value<5) this.redirect=true;
    })
  }


  saveCard() {
    console.log('start interval in payment-form:',new Date());
    this.cardDataEvent.emit(this.cardForm.value)
  }
  cancel() {
    this.cancelEvent.emit(null)
  }

  dateInvalid() {
    if(this.cardForm.invalid){
      this.errStatusMassage='Заполните все необходимые поля';
      return;
    }
    this.expInvalid = false;
    if (this.cardForm.get('expirationMonth').value !== null && this.cardForm.get('expirationYear').value !== null) {
      setTimeout(() => {
        if (this.cardForm.get('expirationMonth').invalid || this.cardForm.get('expirationYear').invalid) {
          this.expInvalid = true;
          this.cardForm.get('expirationMonth').reset();
          this.cardForm.get('expirationYear').reset();
        }
      }, 200);
    }
  }
  getErrMassage(){
    return this.bankErrorCodesService.getErrMassage(this.validityErrorCode)
  }
  cardHolderNameEnabled(){
   return window.location.pathname==='/payment'
  }
}
