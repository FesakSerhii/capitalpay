import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {UserCardService} from '../service/user-card.service';
import {ActivatedRoute} from '@angular/router';
const valid = require("card-validator");

@Component({
  selector: 'app-payment-form',
  templateUrl: './payment-form.component.html',
  styleUrls: ['./payment-form.component.scss']
})
export class PaymentFormComponent implements OnInit {
  cardForm = new FormGroup({
    // cardHolderName: new FormControl(),
    cardNumber: new FormControl(),
    expirationMonth: new FormControl(),
    expirationYear: new FormControl(),
    cvv2Code: new FormControl(),
  });
  token: string = null;
  id: number = null;
  merchantId: number = null;
  isCardValid: boolean = null;


  constructor(private userCardService:UserCardService,private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe((param) => {
      this.merchantId = +param.get("merchantId");
    })
    this.cardForm.controls.cardNumber.valueChanges.subscribe(cardNumber=>{
      const isCardValid = valid.number(cardNumber)
      console.log(isCardValid);
      if(!isCardValid.isPotentiallyValid){
        this.cardForm.controls.cardNumber.setErrors({'invalid card number': true},{emitEvent:false})
      }
    })
    // this.cardForm.controls.expirationMonth.valueChanges.subscribe(expirationMonth=>{
    //   console.log(expirationMonth);
    //   const isCardValid = valid.expirationMonth('03/24')
    //   console.log(isCardValid);
    //   if(!isCardValid.isPotentiallyValid){
    //     this.cardForm.controls.expirationMonth.setErrors({'invalid expirationMonth': true},{emitEvent:false})
    //   }
    // })
  }
  saveCard(){
    this.userCardService.registerCard(this.cardForm.value.cardNumber,this.cardForm.value.expirationYear,this.cardForm.value.expirationMonth,this.cardForm.value.cvv2Code,this.merchantId)
      .then(resp=>{
        this.token = resp.data.token;
        this.id = resp.data.id;
        this.userCardService.cardCheckValidity(this.id).then(response=>{
          this.isCardValid = response.result
        })
      }
    )
  }
}
