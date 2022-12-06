import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-payment-invoicing',
  templateUrl: './payment-invoicing.component.html',
  styleUrls: ['./payment-invoicing.component.scss']
})
export class PaymentInvoicingComponent implements OnInit {
  formInvoicing = new FormGroup({
    sum: new FormControl(),
    consignment: new FormControl(),
    bin: new FormControl(),
    email: new FormControl('', [Validators.email]),
    comment: new FormControl(),
    bankCards: new FormControl()
  });
  constructor() { }

  ngOnInit(): void {
  }

  bankCardsChange(Number: number){
    this.formInvoicing.get('bankCards').patchValue(Number);
  }

}
