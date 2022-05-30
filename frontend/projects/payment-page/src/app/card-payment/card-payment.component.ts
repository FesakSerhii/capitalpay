import {Component, OnInit} from '@angular/core';
import {filter} from 'rxjs/operators';
import {ActivatedRoute} from '@angular/router';
import {RedirectService} from '../service/redirect.service';
import {UserCardService} from '../service/user-card.service';

@Component({
  selector: 'app-card-payment',
  templateUrl: './card-payment.component.html',
  styleUrls: ['./card-payment.component.scss']
})
export class CardPaymentComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute, private userCardService: UserCardService, private redirectService: RedirectService) {
  }

  paymentId: string = null;
  cashBoxInfo: any = null;
  validityErrorCode: string = null;
  validityError: boolean = false;
  queryParamEnable: boolean = false;
  redirectSource: string = null;
  redirect: boolean = false;
  interactionurl: string = null;
  redirectfailed: string = null;
  redirectpending: string = null;
  redirectsuccess: string = null;
  redirecturl: string = null;
  p2pEnabled: boolean = false;

  ngOnInit(): void {
    this.redirectService.redirectEvent.subscribe(value => {
      if (value) this.redirectAction()
    })
    this.activatedRoute.queryParamMap.pipe(filter(param => {
      return param.has('id')||param.has('paymentId')
    })).subscribe(params => {
      this.queryParamEnable = true;
      this.paymentId = params.get('id');
      this.userCardService.getByPaymentId(this.paymentId).then(resp => {
        this.interactionurl = resp.data.interactionurl;
        this.p2pEnabled = resp.data.p2pEnabled;
        this.redirectfailed = resp.data.redirectfailed;
        this.redirectpending = resp.data.redirectpending;
        this.redirectsuccess = resp.data.redirectsuccess;
        this.redirecturl = resp.data.redirecturl;
      })
    })
  }

  getCardData(event) {
    console.log(this.paymentId);
    const data = {
      'paymentId': this.paymentId,
      'cardHolderName': event.cardHolderName,
      'cvv': event.cvv2Code,
      'month': event.expirationMonth,
      'pan': event.cardNumber,
      'year': event.expirationYear,
      'phone': event.phone,
      'email': event.email
    }
    this.userCardService.sendAnonP2pToMerchant(data)
      .then(r => {
        if (r.result) {
          this.redirect = true;

          if (r.data.is3ds) {
            const templateForm = `<FORM  action="${r.data.acsUrl}" METHOD="POST" name="SendOrder" style="visibility:hidden"></br>
    MD<input name="MD" type="text" value="${r.data.md}" /></br>
    PaReq<input name="PaReq" type="text" value="${r.data.paReq}"/>
    </br>
    TermUrl<input name="TermUrl" type="text" value="${r.data.paySystemCallbackUrl}"/></br>
    <input type="submit" name="GotoPay"  value="SEND" /></br>
    <button id="submitButton" type="submit"></button>
</FORM>`
            document.getElementById( 'hiddenFormContainer' ).innerHTML=templateForm;
            const theForm = document.getElementById( 'submitButton' );
            theForm.click();
          } else {
            // this.redirectSource = r.data.is3ds ? r.data.acsUrl : this.redirectsuccess;
            this.redirectSource = this.redirectsuccess;
          }
          this.redirectService.redirectTimerStart()
        } else {
          this.validityError = true;
          this.validityErrorCode = r.data;
          this.redirectSource = this.redirectfailed;
        }
      }).catch(err => {
      switch (err.status) {
        case 500: {
          this.validityError = true;
          this.validityErrorCode = 'Ошибка сервера, попробуйте позже';
          this.redirectSource = this.redirectfailed;
          this.redirectService.redirectTimerStart()
          break;
        }
        case 0: {
          this.validityError = true;
          this.validityErrorCode = 'Отсутствие интернет соединения';
          this.redirectSource = this.redirectfailed;
          this.redirectService.redirectTimerStart()
          break;
        }
        default: {
          this.validityError = true;
          this.validityErrorCode = err.statusMessage;
          this.redirectSource = this.redirectfailed;
          this.redirectService.redirectTimerStart()
          break;
        }
      }
    })
  }

  redirectAction() {
    const link = document.createElement('a')
    link.href = this.redirectSource
    // link.target="_blank"
    link.click()
    console.log(link.href);
  }

  redirectFailed() {
    const link = document.createElement('a')
    link.href = this.redirectfailed;
    // link.target="_blank"
    link.click()
    console.log(link.href);
  }
}
