import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {UserCardService} from '../service/user-card.service';
import {ActivatedRoute} from '@angular/router';
import {validateCard} from '../../../../../common-blocks/validators/paymentCardNumberValidator';
import {expirationDateValidator} from '../../../../../common-blocks/validators/dateCompareValidator';
import {filter} from 'rxjs/operators';
import {BankErrorCodesService} from '../service/bank-error-codes.service';

@Component({
  selector: 'app-payment-form',
  templateUrl: './payment-form.component.html',
  styleUrls: ['./payment-form.component.scss']
})
export class PaymentFormComponent implements OnInit {

  cardForm = new FormGroup({
    cardNumber: new FormControl('', [validateCard()]),
    expirationMonth: new FormControl(),
    expirationYear: new FormControl(),
    cvv2Code: new FormControl(),
  });
  token: string = null;
  id: number = null;
  merchantId: number = null;
  cashBoxId: number = null;
  cashBoxInfo: any = null;
  limitedInterval: any = null;
  isCardValid: boolean = false;
  expInvalid: boolean = false;
  redirect: boolean = false;
  validityError: boolean = false;
  validityErrorCode: boolean = false;
  redirectTimer: number = 5;
  parameters: string = null;
  queryParamEnable: boolean = false;
  acceptTerms = new FormControl();


  constructor(private userCardService: UserCardService, private activatedRoute: ActivatedRoute, public bankErrorCodesService:BankErrorCodesService) {
  }

  ngOnInit(): void {
    let dateFields = {
      expirationMonth: 'expirationYear',
    };
    for (let v in dateFields) {
      this.cardForm.get(v).setValidators([Validators.required, expirationDateValidator(this.cardForm, dateFields[v])]);
      this.cardForm.get(dateFields[v]).setValidators([Validators.required, expirationDateValidator(this.cardForm, v, true)]);
    }
    throw this.activatedRoute.queryParamMap.pipe(filter(param => {
      return !location.href.includes('merchantId') || param.has('merchantId')
    })).subscribe(params => {
      this.queryParamEnable = true;
      this.merchantId = +params.get('merchantId');
      this.cashBoxId = +params.get('cashBoxId');
      this.parameters = params.get('parameters');
      this.userCardService.getCashBoxInfo(this.cashBoxId).then(resp => {
        this.cashBoxInfo = resp.data
        if (this.cashBoxInfo && !this.cashBoxInfo.p2pEnabled) {
          this.redirectTimerStart('redirectfailed')
        }
      })
    })
  }

  redirectTimerStart(type) {
    this.limitedInterval = setInterval(() => {
      if (this.redirectTimer) {
        this.redirectTimer = this.redirectTimer - 1;
      } else {
        clearInterval(this.limitedInterval);
        const link = document.createElement('a')
        link.href = this.cashBoxInfo[type]
        link.click()
      }
    }, 1000)
  }

  saveCard() {
    this.userCardService.registerCard(this.cardForm.value.cardNumber, this.cardForm.value.expirationYear, this.cardForm.value.expirationMonth, this.cardForm.value.cvv2Code, this.merchantId, this.cashBoxId, this.parameters)
      .then(resp => {
        // console.log(resp);
        this.token = resp.data.token;
          this.id = resp.data.id;
          this.userCardService.cardCheckValidity(this.id).then(response => {
            console.log(response);
            this.isCardValid = response.result
            if(!response.data.valid){
              throw new Error(response.data.returnCode)
            }
          }).then(() => {
            this.redirect = true;
            this.redirectTimerStart('redirectsuccess')
          }).catch(err=>{
            this.validityError = true;
            this.validityErrorCode = err.message;
          })
        }
      )
  }

  dateInvalid() {
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

  redirectFailed() {
    const link = document.createElement('a')
    link.href = this.cashBoxInfo.redirectfailed
    link.click()
  }
  getErrMassage(){
   return this.bankErrorCodesService.getErrMassage(this.validityErrorCode)
  }
}
