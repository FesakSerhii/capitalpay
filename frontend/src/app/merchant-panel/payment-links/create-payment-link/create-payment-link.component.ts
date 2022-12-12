import {Component, OnInit, ViewChild} from "@angular/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {CashBoxService} from "../../../../../projects/admin-panel/src/app/service/cashbox.service";
import {CheckFormInvalidService} from "../../../service/check-form-invalid.service";
import {KycService} from "../../../../../projects/admin-panel/src/app/service/kyc.service";
import {UserService} from "../../../../../projects/admin-panel/src/app/service/user.service";
import {PaymentService} from "../../../service/payment.service";
import {FileService} from "../../../service/file.service";
import {jsPDF} from "jspdf";
import {MassageModalComponent} from "../../../../../common-blocks/massage-modal/massage-modal.component";

@Component({
  selector: 'app-create-payment-link',
  templateUrl: './create-payment-link.component.html',
  styleUrls: ['./create-payment-link.component.scss']
})
export class CreatePaymentLinkComponent implements OnInit {
  @ViewChild('massageModal', { static: false }) massageModal: MassageModalComponent;
  merchantId: number = null;
  cashBoxId: any = null;
  linkData: any = null;
  fileList: any[] = [];
  cashBoxListAll: any[] = [];
  fileToUpload: File = null;
  fileListPreview: any[] = [];
  form = new FormGroup({
    merchantName: new FormControl(null, Validators.required),
    merchantEmail: new FormControl(null, Validators.required),
    cashBoxId: new FormControl(null, Validators.required),
    description: new FormControl(null, Validators.required),
    billId: new FormControl(null, Validators.required),
    totalAmount: new FormControl(null, Validators.required),
    payerEmail: new FormControl(null, Validators.required),
    emailTitle: new FormControl(null, Validators.required),
    emailText: new FormControl(null, Validators.required),
    validHours: new FormControl(3, Validators.required),
    fileIds: new FormControl(null),
  });
  errStatusMassage: string = null;
  constructor(private route: ActivatedRoute, private cashBoxService: CashBoxService, public checkFormInvalidService:CheckFormInvalidService, private kycService: KycService, private userService: UserService, private paymentService: PaymentService, private fileService: FileService) {}

  ngOnInit(): void {
    this.merchantId = this.userService.getUserInfo().merchantId;
    this.cashBoxService.getCashBoxListByMerchantId(this.merchantId).then(resp => {
      this.cashBoxListAll = resp.data.map(a => {
        return {value: a.id, title: a?.name}
      });
    })
    this.getUserInfo();
  }

  getUserInfo() {
    this.kycService.getKycInfo(this.merchantId).then(resp => {
      this.form.get("merchantName").patchValue(resp.data.userName);
      this.form.get("merchantEmail").patchValue(resp.data.email);
    })
  }

  sendForm() {
    if (this.form.invalid) {
      return this.form.markAllAsTouched();
    }
    this.form.get('fileIds').patchValue(this.fileList);
    this.paymentService.postPaymentLinkCreate(this.form.value).then(rest => {
      this.linkData = rest.data
    }).catch(error => {
      switch (error.status) {
        case 113: this.errStatusMassage = 'Касса не найдена'; break;
        case 115: this.errStatusMassage = 'Слишком длинный номер счета (больше 31 символа)'; break;
        case 116: this.errStatusMassage = 'Такой номер счета для данной кассы уже существует'; break;
        default: this.errStatusMassage = error.statusMessage; break;
      }
      this.massageModal.open();
      console.log(error);
    })
  }

  handleFileInput(files: FileList) {
    this.fileToUpload = files.item(0);

    if (this.fileToUpload) {
      this.fileService.sendFile(this.fileToUpload).then(resp => {
        this.fileList.push(resp.data.id)
        this.fileListPreview.push({data: resp.data, type: resp.type})
      });
    }
  }

  deleteFileBeforeSaving(id) {
    this.fileList = this.fileList.filter(el => el !== id)
    this.fileListPreview = this.fileListPreview.filter(el => el.id !== id)
  }

  downloadPdf() {
    const doc: any = new jsPDF();
    doc.addImage(this.linkData?.qrCode, 'JPG', 10, 10);
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
  isInvalid(form: FormGroup|FormControl,field: string='') {
    return this.checkFormInvalidService.isInvalid(form,field);
  }
  modalClosed(event: any) {

  }
}
