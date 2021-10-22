import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../service/user.service';
import {KycService} from '../../service/kyc.service';
import {CurrencyService} from '../../service/currency.service';
import {MassageModalComponent} from '../../../../../../common-blocks/massage-modal/massage-modal.component';
import {PaymentsService} from '../../service/payments.service';
import {Subscription} from 'rxjs';
import {ExtValidators} from '../../../../../../src/app/validators/ext-validators';

@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.scss']
})
export class UserSettingsComponent implements OnInit {
  @ViewChild('massageModal', {static: false}) massageModal: MassageModalComponent;

  constructor(private router: Router,
              private userService: UserService,
              private activatedRoute: ActivatedRoute,
              private currencyService: CurrencyService,
              private paymentsService: PaymentsService,
              private kycService: KycService) {
  }

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
    active: new FormControl(),
    blocked: new FormControl(),
    email: new FormControl('', [Validators.email]),
    username: new FormControl('', [Validators.required, Validators.minLength(11), Validators.maxLength(11)]),
    realname: new FormControl(''),
    fio: new FormControl(''),
  });
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
    iinbin: new FormControl('', [ExtValidators.IIN]),
    uaddress: new FormControl(''),
    commission: new FormControl('')
  })
  userRolesForm = new FormGroup({
    ROLE_USER: new FormControl(),
    ROLE_OPERATOR: new FormControl(),
    ROLE_ADMIN: new FormControl(),
    ROLE_MERCHANT: new FormControl(),
  })
  modalTypes = {
    deleteConfirm: 'userDeleteConfirmation',
    blockConfirm: 'userBlockSuccessful',
    delete: 'userDeleteSuccessful',
    block: 'userBlockSuccessful',
    activate: 'userActivateSuccessful',
  }
  modalType = '';
  isEditMode: boolean;
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
  userId: number;
  activeTab: string = 'tab1';
  cashBoxList = new FormArray([])
  regEx = '/(^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$)||(^[+-]?([0-9]+([,][0-9]*)?|[.][0-9]+)$)/gm'

  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe((param) => {
      this.userId = +param.get('userId');
      this.getUserInfo();
      this.getCommissions();
    });

    this.isEditMode = false;
  }

  navigateToSettings() {
    this.router.navigate(['/admin-panel/user'])
  }

  getUserInfo() {
    this.userService.getUserData(this.userId).then(resp => {
      this.userInfoForm.patchValue(resp.data, {emitEvent: false});
      const roles = resp.data.roles;
      for (const role in roles) {
        this.userRoles[roles[role].authority] = true;
        this.userRolesForm.controls[roles[role].authority].setValue(true, {emitEvent: false})
      }

      if (this.userRoles.ROLE_MERCHANT) {
        this.kycService.getKycInfo(this.userId).then(resp => {
          this.merchantInfoForm.patchValue(resp.data);
          this.merchantInfoForm.controls.merchantId.patchValue(this.userId, {emitEvent: false});
          this.merchantInfoForm.controls.mainphone.patchValue(resp.data.mainphone ? resp.data.mainphone.replace('+', '').replace(/\s/g, '') : '');
          this.merchantInfoForm.controls.username.patchValue(resp.data.phone ? resp.data.phone.replace('+', '').replace(/\s/g, '') : '');
        })
        this.currencyService.getCurrencies().then(resp => {
          this.currencies = resp.data;
          for (const currency of this.currencies) {
            this.currenciesForm.addControl(currency.alpha, new FormControl())
          }
        });
        this.paymentsService.getPaymentMethods().then(resp => {
          this.paymentMethods = resp.data;
          for (const paymentMethod of this.paymentMethods) {
            this.paymentMethodsForm.addControl(paymentMethod.name, new FormControl())
          }
        });
        this.currencyService.getMerchantCurrencies(this.userId).then(resp => {
          for (const currency in resp.data) {
            this.currenciesForm.controls[currency].setValue(resp.data[currency])
          }
        })
        this.paymentsService.getMerchantPaymentMethods(this.userId).then(resp => {
          for (const paymentMethod of resp.data) {
            this.paymentMethodsForm.controls[paymentMethod.name].setValue(true)
          }
        })
      }
    })
  }

  async getCommissions() {
    const data = {...await this.userService.getUsersCommissions(this.userId)}.data
    for (const cashBox of data) {
      const form = new FormGroup({
        cashBoxId: new FormControl(cashBox['cashBoxId']),
        cashBoxName: new FormControl(cashBox['cashBoxName']),
        clientFee: new FormControl(cashBox['clientFee']),
        merchantFee: new FormControl(cashBox['merchantFee']),
        totalFee: new FormControl(cashBox['totalFee'])
      })
      this.cashBoxList.controls.push(form);
    }
  }

  editUserData() {
    if (this.userRoles.ROLE_MERCHANT) {
      this.merchantInfoForm.value.phone = this.merchantInfoForm.value.phone ? '+' + this.merchantInfoForm.value.phone : null;
      this.merchantInfoForm.value.mainphone = this.merchantInfoForm.value.mainphone ? '+' + this.merchantInfoForm.value.mainphone : null;
      this.kycService.setKycInfo(this.merchantInfoForm.value).then(resp => {
        this.getUserInfo()
      })
    } else {
      this.userInfoForm.value.phone = '+' + this.userInfoForm.value.phone;
      this.userService.editUserData(this.userInfoForm.value).then(resp => {
        this.getUserInfo()
      })
    }
    this.isEditMode = false;
  }

  deleteOrBlockUser(action) {
    this.modalType = this.modalTypes[action + 'Confirm']
    this.massageModal.open().then(() => {
      this.userService[action + 'User'](this.userId).then(() => {
        this.modalType = this.modalTypes[action]
        this.massageModal.open().then(() => {
          action === 'delete' ? this.navigateToSettings() : this.getUserInfo();
        })
      })
    })
  }

  activateUser() {
    this.userService.activateUser(this.userId).then(() => {
      this.modalType = this.modalTypes['activate']
      this.massageModal.open()
      this.getUserInfo();
    })
  }

  editUserCurrenciesList() {
    const obj = {
      merchantId: this.userId,
      currencyList: []
    }
    for (const currency in this.currenciesForm.value) {
      if (this.currenciesForm.value.hasOwnProperty(currency) && this.currenciesForm.value[currency]) {
        obj.currencyList.push(currency);
      }
    }
    this.currencyService.editUsersCurrenciesList(obj).then(() => {
      this.getUserInfo();
    })
  }

  editUsersPaymentMethodsList() {
    const obj = {
      merchantId: this.userId,
      paysystemList: []
    }
    for (const paymentMethod in this.paymentMethodsForm.value) {
      if (this.paymentMethodsForm.value.hasOwnProperty(paymentMethod) && this.paymentMethodsForm.value[paymentMethod]) {
        obj.paysystemList.push(this.paymentMethods.filter(el => el.name === paymentMethod)[0].id);
      }
    }
    this.paymentsService.editMerchantPaymentMethodsList(obj).then(() => {
      this.getUserInfo();
    })
  }

  changeRole(role) {
    console.log(this.userRolesForm.controls[role].value);
    this.userRolesForm.controls[role].patchValue(!this.userRolesForm.controls[role].value)
    console.log(this.userRolesForm.controls[role].value);
  }

  saveCashBoxFee(formGroup) {
    // let data = {
    //     "merchantId": this.userId,
    //     "cashBoxId": formGroup.cashBoxId,
    //     "merchantFee": formGroup.merchantFee,
    //     "clientFee": formGroup.clientFee
    //   };
    // this.userService.editUsersCommissions(data).then(()=>{
    //   this.getCommissions()
    // })
    let newFees = [...this.cashBoxList.controls.map(el => el.value)]
    newFees = newFees.map(el => {
      return {
        cashBoxId: el.cashBoxId,
        merchantFee: el.merchantFee,
        clientFee: el.clientFee
      }
    })
    let data = {
      'merchantId': this.userId,
      'feeList': newFees
    }
    this.userService.editUsersCommissions(data).then(() => {
      this.getCommissions()
    })
  }

  replaceSymbols(string) {
    return string.includes('.') ? string.trim().replace(',', '') : string.trim().replace(',', '.')
  }

  saveRoles() {
    const newRoles = {
        'userId': this.userId,
        'roleList': []
      }
    ;
    for (const roleValue in this.userRolesForm.value) {
      if (this.userRolesForm.value[roleValue]) {
        newRoles.roleList.push(roleValue)
      }
    }
    this.userService.changeUserRolesList(newRoles).then(() => {
      this.getUserInfo()
    })
  }
}
