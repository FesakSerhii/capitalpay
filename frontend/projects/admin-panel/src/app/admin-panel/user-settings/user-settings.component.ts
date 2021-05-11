import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../service/user.service";
import {KycService} from "../../service/kyc.service";
import {CurrencyService} from "../../service/currency.service";
import {MassageModalComponent} from "../../../../../../common-blocks/massage-modal/massage-modal.component";
import {PaymentsService} from "../../service/payments.service";

@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.scss']
})
export class UserSettingsComponent implements OnInit {
  @ViewChild("massageModal", {static: false}) massageModal: MassageModalComponent;

  constructor(private router: Router,
              private userService: UserService,
              private activatedRoute: ActivatedRoute,
              private currencyService: CurrencyService,
              private paymentsService: PaymentsService,
              private kycService: KycService) { }
// {
//   "id":18,
//   "password":"blablabla",
//   "email":"user125@gmail.com",
//   "phone":"+77096384345",
//   "realname":"Федоров Михаил",
//   "active": true,
//   "blocked": false,
// }
  userInfoForm = new FormGroup({
    id: new FormControl(),
    password: new FormControl(),
    email: new FormControl('',[Validators.email]),
    phone: new FormControl('',[Validators.minLength(11),Validators.maxLength(11)]),
    realname: new FormControl(''),
    fio: new FormControl(''),
  });
  merchantInfoForm = new FormGroup({
    merchantId: new FormControl(),
    password: new FormControl(),
    email: new FormControl('',[Validators.email]),
    phone: new FormControl('',[Validators.minLength(11),Validators.maxLength(11)]),
    realname: new FormControl(''),
    active: new FormControl(),
    blocked: new FormControl(),
    faddress: new FormControl(''),
    headname: new FormControl(''),
    accountant: new FormControl(''),
    bik: new FormControl(''),
    mainphone: new FormControl(''),
    iik: new FormControl(''),
    mname: new FormControl(''),
    bankname: new FormControl(''),
    iinbin: new FormControl(''),
    uaddress: new FormControl('')
  })
  userRolesForm = new FormGroup({
    ROLE_USER:  new FormControl(),
    ROLE_OPERATOR: new FormControl(),
    ROLE_ADMIN: new FormControl(),
    ROLE_MERCHANT: new FormControl(),
  })
  modalTypes ={
    confirm:'userDeleteConfirmation',
    delete:'userDeleteSuccessful'
  }
  modalType = this.modalTypes.confirm;
  isEditMode:boolean;
  userRoles = {
    ROLE_USER: false,
    ROLE_OPERATOR: false,
    ROLE_ADMIN: false,
    ROLE_MERCHANT: false
  };
  currencies: any;
  currenciesForm = new FormGroup({});
  paymentMethods: any;
  paymentMethodsForm = new FormGroup({});
  userId: string;
  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe((param) => {
      this.userId = param.get("userId");
      this.getUserInfo(this.userId);
    });
    this.isEditMode = false;
  }
  navigateToSettings(){
    this.router.navigate(['/admin-panel/user'])
  }
  getUserInfo(id){
    this.userService.getUserData(id).then(resp=>{
      this.userInfoForm.patchValue(resp.data);
      let roles = resp.data.roles;
      for(let role in roles){
        this.userRoles[roles[role].authority] = true;
        this.userRolesForm.controls[roles[role].authority].setValue(true)
      }
      if(this.userRoles.ROLE_MERCHANT){
        this.kycService.getKycInfo(id).then(resp=>{
          this.merchantInfoForm.patchValue(resp.data);
          this.merchantInfoForm.controls.merchantId.patchValue(this.userId);
        })
        this.currencyService.getCurrencies().then(resp=>{
          this.currencies = resp.data;
          for(let currency of this.currencies){
            this.currenciesForm.addControl(currency.alpha, new FormControl())
          }
        });
        this.paymentsService.getPaymentMethods().then(resp=>{
          this.paymentMethods = resp.data;
          for(let paymentMethod of this.paymentMethods){
            this.paymentMethodsForm.addControl(paymentMethod.name, new FormControl())
          }
        });
        this.currencyService.getMerchantCurrencies(id).then(resp=>{
          for(let currency in resp.data){
            this.currenciesForm.controls[currency].setValue(resp.data[currency])
          }
        })
        this.paymentsService.getMerchantPaymentMethods(id).then(resp=>{
          for(let paymentMethod of resp.data){
            console.log(paymentMethod);
            this.paymentMethodsForm.controls[paymentMethod.name].setValue(true)
          }
        })
      }
    })
  }
  editUserData() {
    if(this.userRoles.ROLE_MERCHANT){
      this.kycService.setKycInfo(this.merchantInfoForm.value).then(resp=>{
        this.getUserInfo(this.userId)
      })
    }else{
      this.userService.editUserData(this.userInfoForm.value).then(resp=>{
        this.getUserInfo(this.userId)
      })
    }
    this.isEditMode = false;
  }
  deleteUser(){
      this.massageModal.open().then(()=>{
        this.userService.deleteUser(this.userId).then(()=>{
        this.modalType = this.modalTypes.delete
        this.massageModal.open().then(()=>{
        this.navigateToSettings();
        })
      })
    })
  }

  editUserCurrenciesList() {
    let obj = {
      "merchantId": this.userId,
      "currencyList": []
    }
    for(let currency in this.currenciesForm.value){
      if(this.currenciesForm.value.hasOwnProperty(currency)&&this.currenciesForm.value[currency]){
        obj.currencyList.push(currency);
      }
    }
    this.currencyService.editUsersCurrenciesList(obj).then(()=>{
      this.getUserInfo(this.userId);
    })
  }

  editUsersPaymentMethodsList() {
    let obj = {
      "merchantId": this.userId,
      "paysystemList": []
    }
    for(let currency in this.currenciesForm.value){
      if(this.currenciesForm.value.hasOwnProperty(currency)&&this.currenciesForm.value[currency]){
        console.log(this.paymentMethods.filter(el => el.name === currency)[0]);
        // obj.paysystemList.push();
      }
    }
    this.paymentsService.editMerchantPaymentMethodsList(obj).then(()=>{
      this.getUserInfo(this.userId);
    })
  }
}
