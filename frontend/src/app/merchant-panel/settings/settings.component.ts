import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {CurrencyService} from '../../../../projects/admin-panel/src/app/service/currency.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {CashBoxService} from '../../../../projects/admin-panel/src/app/service/cashbox.service';
import {PaymentsService} from '../../../../projects/admin-panel/src/app/service/payments.service';
import {RegisterService} from '../../service/register.service';
import {UserService} from '../../../../projects/admin-panel/src/app/service/user.service';
import {KycService} from '../../../../projects/admin-panel/src/app/service/kyc.service';
import {ConfirmActionModalComponent} from '../../../../common-blocks/confirm-action-modal/confirm-action-modal.component';
import {ActivatedRoute} from '@angular/router';
import {CheckFormInvalidService} from "../../service/check-form-invalid.service";
import {error} from "protractor";
import {jsPDF} from "jspdf";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  @ViewChild('addBoxOffice', {static: false}) addBoxOffice: TemplateRef<any>;
  @ViewChild('changePassword', {static: false}) changePasswordModal: TemplateRef<any>;
  @ViewChild('paymentLink', {static: false}) paymentLinkModal: TemplateRef<any>;
  @ViewChild('confirmContent', {static: false}) confirmModal: ConfirmActionModalComponent;

  constructor(private modalService: NgbModal,
              private cashBoxService: CashBoxService,
              private currencyService: CurrencyService,
              private registerService: RegisterService,
              private userService: UserService,
              private kycService: KycService,
              public checkFormInvalidService:CheckFormInvalidService,
              private activatedRoute: ActivatedRoute,
              private paymentsService: PaymentsService) {
  }


  merchantInfoForm = new FormGroup({
    merchantId: new FormControl(),
    password: new FormControl(),
    email: new FormControl('', [Validators.email]),
    username: new FormControl('', [Validators.minLength(11), Validators.maxLength(11)]),
    realname: new FormControl(''),
    active: new FormControl(),
    blocked: new FormControl(),
    faddress: new FormControl(''),
    headname: new FormControl(''),
    accountant: new FormControl(''),
    bik: new FormControl(''),
    mainphone: new FormControl('', [Validators.minLength(11), Validators.maxLength(11)]),
    iik: new FormControl(''),
    mname: new FormControl(''),
    bankname: new FormControl(''),
    iinbin: new FormControl(''),
    uaddress: new FormControl(''),
    commission: new FormControl('')
  })
  activeTab: string = 'tab1';
  currencyList: any = null;
  activePage: any = null;
  currenciesList: any = null;
  currencies: any = null;
  paymentMethods: any = null;
  merchantLinkInfo: any = null;
  cashBoxInfo = new FormGroup({
    redirectfailed: new FormControl(''),
    redirectsuccess: new FormControl(''),
    redirecturl: new FormControl(''),
    interactionurl: new FormControl(''),
    redirectpending: new FormControl(''),
    client_fee: new FormControl(''),
    secret: new FormControl(''),
    merchantId: new FormControl(''),
    cashboxId: new FormControl('')
  });
  currenciesForm = new FormGroup({})
  casBoxSettingsForm = new FormGroup({
    name: new FormControl()
  });
  passChangeForm = new FormGroup({
    oldPassword: new FormControl(),
    newPassword: new FormControl(),
    confirmPassword: new FormControl(),
  });
  cashBoxForm = new FormGroup({
    name: new FormControl(),
    currency: new FormControl()
  })
  paymentLinkForm = new FormGroup({
    cashBoxId: new FormControl(null, [Validators.required]),
    companyName: new FormControl(null, [Validators.required]),
    contactPhone: new FormControl(null, [Validators.required]),
  })
  editPaymentLinkForm = new FormGroup({
    id: new FormControl(null, [Validators.required]),
    cashBoxId: new FormControl(null, [Validators.required]),
    companyName: new FormControl(null, [Validators.required]),
    contactPhone: new FormControl(null, [Validators.required]),
    link: new FormControl(),
    qr: new FormControl(),
  })
  cashBoxList: any = null;
  cashBoxMenuOpened: boolean = false;
  cashBoxEditOpened: boolean = false;
  selectedCashBoxId: any = null;
  isFirstLoad: boolean = true;
  paymentMethodsForm = new FormGroup({});
  merchantId: number = null;
  confirmMassage: string = null;
  isTwoFactor = new FormControl();
  errStatusMassage: string = null;
  paymentLinkSuccessEdit: boolean;

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe((param) => {
      console.log(param.get('info') ? 'tab3' : 'tab1');
      this.activeTab = param.get('info') ? 'tab3' : 'tab1'
    });
    this.merchantId = this.userService.getUserInfo().merchantId;
    this.getCurrency()
    this.getCashBoxList();
    this.getUserInfo();
    this.isTwoFactorActive();
    // this.getPaymentsMethod()
  }

  getCurrency() {
    this.currencyService.getCurrencies().then(resp => {
      this.currencies = resp.data.map(el => el.alpha);
    })
  }

  getUserInfo() {
    this.kycService.getKycInfo(this.merchantId).then(resp => {
      this.merchantInfoForm.patchValue(resp.data);
      this.merchantInfoForm.controls.mainphone.patchValue(resp.data.mainphone ? resp.data.mainphone.replace('+', '').replace(/\s/g, '') : '');
      this.merchantInfoForm.controls.username.patchValue(resp.data.phone ? resp.data.phone.replace('+', '').replace(/\s/g, '') : '');

    })
  }

  getCashBoxList() {
    this.cashBoxService.getCashBoxListAll().then(resp => {
      this.cashBoxList = resp.data.filter(el => {
        return el.merchantId === this.merchantId
      });
    })
  }

  open(modal) {
    this.modalService.open(modal, {windowClass: ''});
  }

  async paymentLinkOpen(modal, box) {
    try {
      this.merchantLinkInfo = await this.cashBoxService.getMerchantLinkGetPublicInfo(box.id);
      this.modalService.dismissAll();
      if(!this.merchantLinkInfo.result) {
        this.paymentLinkForm.get('cashBoxId').patchValue(box.id);
        this.paymentLinkForm.get('companyName').patchValue(box.name);
      } else {
        this.editPaymentLinkForm.patchValue(this.merchantLinkInfo.data)
      }
      this.modalService.open(modal, {windowClass: ''});
    } catch (error) {}
  }

  createPaymentLink() {
    if(this.paymentLinkForm.invalid){
      return;
    }
    this.cashBoxService.postMerchantLinkCreate(this.paymentLinkForm.value).then(rest => {
      // this.modalService.dismissAll()
      this.paymentLinkForm.reset();
      this.merchantLinkInfo = rest;
      this.editPaymentLinkForm.patchValue(rest.data)
    })

  }
  editPaymentLink() {
    if(this.editPaymentLinkForm.invalid){
      return;
    }
    const obj:any = Object.fromEntries(Object.entries(this.editPaymentLinkForm.value).filter(([key, value]) => key === 'id' || key === 'companyName' || key === 'contactPhone'))
    this.cashBoxService.postMerchantLinkEdit(obj).then(rest => {
      this.merchantLinkInfo = rest;
      this.editPaymentLinkForm.reset();
      this.editPaymentLinkForm.patchValue(rest.data)
      this.paymentLinkSuccessEdit = true;
      setTimeout(() => {
        this.paymentLinkSuccessEdit = false;
      }, 3000)
    })
  }

  editCashBoxName() {
    const obj = {
      'cashboxId': this.selectedCashBoxId,
      'name': this.casBoxSettingsForm.value.name
    }
    if (this.casBoxSettingsForm.value.name === this.cashBoxList.filter(el => el.id === this.selectedCashBoxId)[0].name) {
      // this.cashBoxEditOpened = false;
      // this.selectedCashBoxId = null;
      this.editCashBoxEnabled()
    } else {
      this.cashBoxService.editCashBox(obj).then(resp => {
        // this.cashBoxEditOpened = false;
        // this.getCashBoxList()
        this.editCashBoxEnabled()
      })
    }
  }

  editCashBoxInfo() {
    const data = {
      cashBoxId: this.selectedCashBoxId,
      fields: []
    }
    // this.cashBoxInfo.value.forEach()
    for (const field in this.cashBoxInfo.value) {
      data.fields.push({
        'cashBoxId': this.selectedCashBoxId,
        'fieldName': field,
        'fieldValue': this.cashBoxInfo.value[field]
      })
    }
    this.cashBoxService.editCashBoxInfo(data).then(resp => {
      this.cashBoxEditOpened = false;
      this.selectedCashBoxId = null;
      this.getCashBoxList()
    })
  }

  openCashBoxSettings() {
    this.casBoxSettingsForm.controls.name.setValue(this.cashBoxList.filter(el => el.id === this.selectedCashBoxId)[0].name)
  }

  addCashBox() {
    this.cashBoxService.addCashBox(this.cashBoxForm.value).then(() => {
      this.getCashBoxList()
      this.modalService.dismissAll()
    })
    this.modalService.dismissAll()
  }

  async getCurrencies() {
    this.openCashBoxSettings()
    this.currenciesList = {...await this.cashBoxService.getCashBoxCurrencyList(this.selectedCashBoxId)}.data
    this.paymentMethods = {...await this.cashBoxService.getCashBoxPaymentList(this.selectedCashBoxId)}.data
    this.cashBoxInfo.patchValue({...await this.cashBoxService.getCashBoxInfo(this.selectedCashBoxId)}.data)
    for (const currency of this.currenciesList) {
      this.currenciesForm.addControl(currency.alpha, new FormControl(currency.enabled))
    }
    for (const paymentMethod of this.paymentMethods) {
      this.paymentMethodsForm.addControl(paymentMethod.id, new FormControl(paymentMethod.enabled))
    }
    this.isFirstLoad = false;
    // this.cashBoxEditOpened = true;
    if (!this.cashBoxInfo.value.secret) {
      let newSecretFromToken = ''
      let characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      let charactersLength = characters.length;
      for (let i = 0; i < 20; i++) {
        newSecretFromToken += characters.charAt(Math.floor(Math.random() *
          charactersLength));
      }
      this.cashBoxInfo.controls.secret.patchValue(newSecretFromToken)
    }
    this.cashBoxEditOpened = true;
  }

  editCashBoxEnabled() {
    const dataCurrency = {
      cashboxId: this.selectedCashBoxId,
      currencyList: [],
    }
    const dataPaymentMethod = {
      cashboxId: this.selectedCashBoxId,
      paysystemList: []
    }
    for (const currency of this.currenciesList) {
      if (this.currenciesForm.controls[currency.alpha].value) {
        dataCurrency.currencyList.push(currency.alpha)
      }
    }
    for (const paymentMethod of this.paymentMethods) {
      if (this.paymentMethodsForm.controls[paymentMethod.id].value) {
        dataPaymentMethod.paysystemList.push(paymentMethod.id)
      }
    }
    const promises = []
    promises.push(this.cashBoxService.editEnableCurrecies(dataCurrency));
    promises.push(this.cashBoxService.editCashBoxPaymentList(dataPaymentMethod));
    // this.cashBoxService.editEnableCurrecies(dataCurrency).then(() => {
    //   this.getCurrencies();
    //   this.selectedCashBoxId = null;
    // })
    // this.cashBoxService.editCashBoxPaymentList(dataPaymentMethod).then(() => {
    //   this.getCurrencies();
    //   this.selectedCashBoxId = null;
    // })
    Promise.all(promises).then(()=>{
      this.editCashBoxInfo()
    })
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
  confirmPasswordChange() {
    if(this.passChangeForm.invalid){
      this.errStatusMassage='?????????????????? ?????? ?????????????????????? ????????';
      return;
    }
    this.confirmMassage = 'changePassword'
    this.confirmModal.open().then(resp => {
      this.registerService.changePassword(this.passChangeForm.value.oldPassword, this.passChangeForm.value.newPassword).catch(err => {
        switch (err.status) {
          case 500: this.errStatusMassage = '???????????? ??????????????, ???????????????????? ??????????'; break;
          case 0: this.errStatusMassage = '???????????????????? ???????????????? ????????????????????'; break;
          default: this.errStatusMassage = err.statusMessage; break;
        }
      })
    })
  }

  comparePasswords() {
    return (this.passChangeForm.value.newPassword && this.passChangeForm.value.confirmPassword) && (this.passChangeForm.value.newPassword === this.passChangeForm.value.confirmPassword)
  }

  deleteCashBox() {
    this.confirmMassage = 'deleteCashBoxConfirmation'
    this.confirmModal.open().then(resp => {
      this.cashBoxService.deleteCashBox(this.selectedCashBoxId).then(() => {
        this.getCashBoxList();
        this.selectedCashBoxId = null;
      })
    })
  }

  async isTwoFactorActive() {
    this.isTwoFactor.patchValue(await this.registerService.twoFactorAuthAvailable());
    this.isTwoFactor.valueChanges.subscribe(val => {
      this.registerService.twoFactorAuthSet(val)
    })
  }
  changeTab(tab){
    this.activeTab=tab;
    this.cashBoxEditOpened=false;
    this.selectedCashBoxId=null
  }

  isInvalid(form: FormGroup|FormControl,field: string='') {
    return this.checkFormInvalidService.isInvalid(form,field);
  }
}
