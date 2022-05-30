import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {CurrencyService} from '../../service/currency.service';
import {FormControl, FormGroup} from '@angular/forms';
import {PaymentsService} from '../../service/payments.service';
import {ConfirmActionModalComponent} from '../../../../../../common-blocks/confirm-action-modal/confirm-action-modal.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {RegisterService} from '../../../../../../src/app/service/register.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  activeTab = 'tab1';
  @ViewChild("confirmContent", {static: false}) confirmModal: ConfirmActionModalComponent;
  @ViewChild('changePassword', {static: false}) changePasswordModal: TemplateRef<any>;


  constructor(private currencyService:CurrencyService,
              private registerService:RegisterService,
              private modalService: NgbModal,
              private activatedRoute: ActivatedRoute,
              private paymentsService:PaymentsService) { }
  currencyList:any=null;
  paymentMethods:any=null;
  passChangeForm = new FormGroup({
    oldPassword: new FormControl(),
    newPassword: new FormControl(),
    confirmPassword: new FormControl(),
  });
  paymentMethodsForm = new FormGroup({});
  currenciesForm = new FormGroup({});
  isFirstLoadCurrency: boolean = true;
  isFirstLoad: boolean = true;
  confirmMassage: string = null;
  isTwoFactor = new FormControl();
  currencyEnabledToSave:any[]=[];
  paymentMethodsEnabledToSave:any[]=[];
  errStatusMassage: string = null;

  ngOnInit(): void {
    this.getCurrencies();
    this.getPaymentsMethod();
    this.isTwoFactorActive();
  }
  async getCurrencies(){
    this.currencyService.getCurrencies().then(resp=>{
      this.currencyList = resp.data;
      this.currencyEnabledToSave = [];
      for(const currency of this.currencyList){
        this.currenciesForm.addControl(currency.alpha, new FormControl(currency.enabled))
      }
    })
  }
  async isTwoFactorActive(){
    this.isTwoFactor.patchValue(await this.registerService.twoFactorAuthAvailable());
    this.isTwoFactor.valueChanges.subscribe(val=>{
      this.registerService.twoFactorAuthSet(val)
    })
  }
  async getPaymentsMethod(){
    this.paymentsService.getPaymentMethods().then(resp=>{
      this.paymentMethods = resp.data;
      this.paymentMethodsEnabledToSave = [];
      for(const paymentMethod of this.paymentMethods){
        this.paymentMethodsForm.addControl(paymentMethod.name, new FormControl(paymentMethod.enabled))
      }
    });
  }
  open(modal){
    this.modalService.open(modal,{windowClass:''});
  }
  comparePasswords(){
    return (this.passChangeForm.value.newPassword&&this.passChangeForm.value.confirmPassword)&&(this.passChangeForm.value.newPassword===this.passChangeForm.value.confirmPassword)
  }
  confirmPasswordChange() {
    if(this.passChangeForm.invalid){
      this.errStatusMassage='Заполните все необходимые поля';
      return;
    }
    this.confirmMassage = 'changePassword'
    this.confirmModal.open().then(resp=> {
      this.registerService.changePassword(this.passChangeForm.value.oldPassword,this.passChangeForm.value.newPassword).catch(err => {
        switch (err.status) {
          case 500: this.errStatusMassage = 'Ошибка сервера, попробуйте позже'; break;
          case 0: this.errStatusMassage = 'Отсутствие интернет соединения'; break;
          default: this.errStatusMassage = err.statusMessage; break;
        }
      })
    })
  }
 async saveEnabledCurrencies(){
   for(const currency of this.currencyEnabledToSave){
     await this.currencyService.editEnableCurrecies(currency)
   }
   this.getCurrencies();
  }
 async saveEnabledPaymentMethods(){
    for(const paymentMethod of this.paymentMethodsEnabledToSave){
      await this.paymentsService.editEnable(paymentMethod)
    }
  this.getPaymentsMethod();
  }

  paymentMethodSet(paymentMethod) {
    console.log(this.paymentMethodsForm);
    const data = {
      paysystemId: paymentMethod.id,
      enabled: this.paymentMethodsForm.value[paymentMethod.name]
    }
    this.paymentMethodsEnabledToSave.filter(el=>el.paysystemId!==paymentMethod.id)
    this.paymentMethodsEnabledToSave.push(data)
  }

  currencySet(currency) {
    const data = {
      alpha: currency.alpha,
      name: currency.name,
      enabled: this.currenciesForm.value[currency.alpha]
    }
    this.currencyEnabledToSave.filter(el=>el.alpha!==currency.alpha)
    this.currencyEnabledToSave.push(data)
  }
}
