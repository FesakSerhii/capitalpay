import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UserCardService} from "../service/user-card.service";
import {ActivatedRoute} from "@angular/router";
import {CheckFormInvalidService} from "../../../../../src/app/service/check-form-invalid.service";
import jsPDF from 'jspdf';

@Component({
  selector: 'app-payment-invoicing',
  templateUrl: './payment-invoicing.component.html',
  styleUrls: ['./payment-invoicing.component.scss']
})
export class PaymentInvoicingComponent implements OnInit {
  formInvoicing = new FormGroup({
    creationLinkId: new FormControl('', [Validators.required]),
    totalAmount: new FormControl('', [Validators.required]),
    billId: new FormControl('', [Validators.required]),
    payerEmail: new FormControl('', [Validators.email]),
    emailTitle: new FormControl('', [Validators.required]),
    emailText: new FormControl('', [Validators.required]),
    description: new FormControl('', [Validators.required]),
    failed: new FormControl(),
    validHours: new FormControl('3'),
  });
  errStatusMassage: string = null;
  paymentLinkForm = new FormGroup({
    companyName: new FormControl(null, [Validators.required]),
    contactPhone: new FormControl(null, [Validators.required]),
    link: new FormControl(),
    qrCode: new FormControl(),
  })
  linkId: string = null;
  successForm: boolean;
  constructor(private userCardService: UserCardService, private activatedRoute: ActivatedRoute, public checkFormInvalidService:CheckFormInvalidService) { }
  ngOnInit(): void {
    this.linkId = this.activatedRoute.snapshot.paramMap.get('link');
    if(this.linkId) {
      this.formInvoicing.get('creationLinkId').patchValue(this.linkId);
      this.userCardService.getPublicInfo(this.linkId).then(rest => {
        this.paymentLinkForm.get('companyName').patchValue(rest.data.companyName);
        this.paymentLinkForm.get('contactPhone').patchValue(rest.data.contactPhone);
        this.formInvoicing.get('emailTitle').patchValue(`Ссылки на оплату счета ${rest.data.companyName}`)
        this.formInvoicing.get('emailText').patchValue(`Ссылки на оплату счета ${rest.data.companyName}`)
      })
    }
  }

  submitForm() {
    if(this.formInvoicing.invalid){
      this.errStatusMassage='Заполните все необходимые поля';
      return;
    }
    this.userCardService.postCreateByLink(this.formInvoicing.value).then(rest => {
      this.successForm = true;
      this.paymentLinkForm.get('qrCode').patchValue(rest.data.qrCode);
      this.paymentLinkForm.get('link').patchValue(rest.data.link);
    })
  }
  isInvalid(form: FormGroup|FormControl,field: string='') {
    return this.checkFormInvalidService.isInvalid(form,field);
  }
  downloadPdf(qrCode) {
    const doc: any = new jsPDF();
    doc.addImage(qrCode, 'JPG', 10, 10);
    doc.save('capitalPay.pdf');
  }
  async copy(text: string, image: boolean = false) {
    if(image) {
      try {
        // @ts-ignore
        await navigator.clipboard.write([
          // @ts-ignore
          new ClipboardItem({
            'image/png': fetch(text).then(res => res.blob())
          })
        ]);
      } catch (err) {
        console.error(err.name, err.message);
      }
    } else {
      navigator.clipboard.writeText(text).then(() => {}).catch(err => {
        console.error(err.name, err.message);
      });
    }

  }
}
