import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {UserCardService} from '../service/user-card.service';
import {ActivatedRoute} from '@angular/router';
import {validateCard} from '../../../../../common-blocks/validators/paymentCardNumberValidator';
import {expirationDateValidator} from '../../../../../common-blocks/validators/dateCompareValidator';

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
  redirectTimer: number = 5;
  parameters: string = null;
  acceptTerms = new FormControl();


  constructor(private userCardService: UserCardService, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe((param) => {
      this.merchantId = +param.get('merchantId');
      this.cashBoxId = +param.get('cashBoxId');
      this.parameters = param.get('parameters');

      this.userCardService.getCashBoxInfo(this.cashBoxId).then(resp => {
        this.cashBoxInfo = resp.data
      })
    })
    let dateFields = {
      expirationMonth: 'expirationYear',
    };
    for (let v in dateFields) {
      this.cardForm.get(v).setValidators([Validators.required, expirationDateValidator(this.cardForm, dateFields[v])]);
      // this.cardForm.get(v).valueChanges.subscribe(()=>this.cardForm.get(dateFields[v]).updateValueAndValidity({emitEvent:false}))
      this.cardForm.get(dateFields[v]).setValidators([Validators.required, expirationDateValidator(this.cardForm, v, true)]);
      // this.cardForm.get(dateFields[v]).valueChanges.subscribe(()=>this.cardForm.get(v).updateValueAndValidity({emitEvent:false}))
    }


    // this.cardForm.controls.expirationMonth.valueChanges.subscribe(expirationMonth=>{
    //   console.log(expirationMonth);
    //   const isCardValid = valid.expirationMonth('04/24')
    //   console.log(isCardValid);
    //   if(!isCardValid.isPotentiallyValid){
    //     this.cardForm.controls.expirationMonth.setErrors({'invalid expirationMonth': true},{emitEvent:false})
    //   }
    // })
  }

  saveCard() {
    this.userCardService.registerCard(this.cardForm.value.cardNumber, this.cardForm.value.expirationYear, this.cardForm.value.expirationMonth, this.cardForm.value.cvv2Code, this.merchantId, this.cashBoxId,this.parameters)
      .then(resp => {
          this.token = resp.data.token;
          this.id = resp.data.id;
          this.userCardService.cardCheckValidity(this.id).then(response => {
            this.isCardValid = response.result
          }).then(() => {
            this.redirect = true;
            this.limitedInterval = setInterval(() => {
              if (this.redirectTimer) {
                this.redirectTimer = this.redirectTimer - 1;
              }else{
                clearInterval(this.limitedInterval);
                const link = document.createElement('a')
                link.href = this.cashBoxInfo.redirectsuccess
                link.click()
              }
            },1000)
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
}
