import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {validateCard} from '../../../../../common-blocks/validators/paymentCardNumberValidator';
import {UserCardService} from '../service/user-card.service';
import {ActivatedRoute} from '@angular/router';
import {BankErrorCodesService} from '../service/bank-error-codes.service';
import {expirationDateValidator} from '../../../../../common-blocks/validators/dateCompareValidator';
import {filter} from 'rxjs/operators';
import {RedirectService} from '../service/redirect.service';

@Component({
  selector: 'app-card-binding',
  templateUrl: './card-binding.component.html',
  styleUrls: ['./card-binding.component.scss']
})
export class CardBindingComponent implements OnInit {

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
  validityErrorCode: string = null;
  redirectSource: string = null;
  parameters: string = null;
  queryParamEnable: boolean = false;
  acceptTerms = new FormControl();
  errStatusMassage: string = null;


  constructor(private userCardService: UserCardService,
              private activatedRoute: ActivatedRoute,
              public bankErrorCodesService: BankErrorCodesService,
              private redirectService: RedirectService) {
  }

  ngOnInit(): void {
    this.redirectService.redirectEvent.subscribe(value => {
      console.log(value);
      if (value) this.redirectAction()
    })
    this.activatedRoute.queryParamMap.pipe(filter(param => {
      return !location.href.includes('merchantId') || param.has('merchantId')
    })).subscribe(params => {
      this.queryParamEnable = true;
      this.merchantId = +params.get('merchantId');
      this.cashBoxId = +params.get('cashBoxId');
      this.parameters = params.get('parameters');
      this.userCardService.getCashBoxInfo(this.cashBoxId).then(resp => {
        this.cashBoxInfo = resp.data
        if (this.cashBoxInfo && !this.cashBoxInfo.p2pEnabled) {
          // this.redirectTimerStart('redirectfailed')
          this.redirectSource = 'redirectfailed';
          this.redirectService.redirectTimerStart()
        }
      }).catch(err => {
        switch (err.status) {
          case 500: {
            this.validityError = true;
            this.validityErrorCode = 'Ошибка сервера, попробуйте позже';
            break;
          }
          case 0: {
            this.validityError = true;
            this.validityErrorCode = 'Отсутствие интернет соединения';
            break;
          }
          default: {
            this.validityError = true;
            this.validityErrorCode = err.statusMessage;
            break;
          }
        }
      })
    })
  }

  // redirectTimerStart(type) {
  //   this.limitedInterval = setInterval(() => {
  //     if (this.redirectTimer) {
  //       this.redirectTimer = this.redirectTimer - 1;
  //     } else {
  //       clearInterval(this.limitedInterval);
  //       const link = document.createElement('a')
  //       link.href = this.cashBoxInfo[type]
  //       link.click()
  //     }
  //   }, 1000)
  // }
  redirectAction() {
    const link = document.createElement('a')
    link.href = this.cashBoxInfo[this.redirectSource]
    // link.target="_blank"
    link.click()
  }

  redirectFailed() {
    const link = document.createElement('a')
    link.href = this.cashBoxInfo.redirectfailed;
    // link.target="_blank"
    link.click()
  }

  getErrMassage() {
    return this.bankErrorCodesService.getErrMassage(this.validityErrorCode)
  }

  getCardData(event: any) {
    this.userCardService.registerCard(event.cardNumber, event.expirationYear, event.expirationMonth, event.cvv2Code, this.merchantId, this.cashBoxId, this.parameters)
      .then(resp => {
          this.token = resp.data.token;
          this.id = resp.data.id;
          this.userCardService.cardCheckValidity(this.id).then(response => {
            this.isCardValid = response.result
            if (!response.data.valid) {
              throw new Error(response.data.returnCode)
            }
          }).then(() => {
            this.redirect = true;
            // this.redirectTimerStart('redirectsuccess')
            this.redirectSource = 'redirectsuccess';
            console.log('start interval in card-binding:', new Date());
            this.redirectService.redirectTimerStart()
          }).catch(err => {
            switch (err.status) {
              case 500: {
                this.validityError = true;
                this.validityErrorCode = 'Ошибка сервера, попробуйте позже';
                break;
              }
              case 0: {
                this.validityError = true;
                this.validityErrorCode = 'Отсутствие интернет соединения';
                break;
              }
              default: {
                this.validityError = true;
                this.validityErrorCode = err.statusMessage;
                break;
              }
            }
          })
        }
      )
  }
}
