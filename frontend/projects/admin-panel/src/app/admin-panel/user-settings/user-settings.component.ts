import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../service/user.service';
import {KycService} from '../../service/kyc.service';
import {CurrencyService} from '../../service/currency.service';
import {MassageModalComponent} from '../../../../../../common-blocks/massage-modal/massage-modal.component';
import {PaymentsService} from '../../service/payments.service';
import {Subscription} from 'rxjs';
import {ExtValidators} from '../../../../../../src/app/validators/ext-validators';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {P2pService} from '../../service/p2p.service';
import {PaymentCardModalComponent} from '../../../../../../common-blocks/payment-card-modal/payment-card-modal.component';
import {map, switchMap} from 'rxjs/operators';
import {SortHelper} from '../../../../../../src/app/helper/sort-helper';
import {SearchInputService} from '../../../../../../src/app/service/search-input.service';

@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.scss']
})
export class UserSettingsComponent implements OnInit {
  @ViewChild('massageModal', {static: false}) massageModal: MassageModalComponent;
  @ViewChild('paymentCard', {static: false}) paymentCard: PaymentCardModalComponent;

  constructor(private router: Router,
              private userService: UserService,
              private activatedRoute: ActivatedRoute,
              private currencyService: CurrencyService,
              private paymentsService: PaymentsService,
              private modalService: NgbModal,
              private p2pService: P2pService,
              private searchInputService: SearchInputService,
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
    deleteCardConfirm: 'cardDeleteConfirmation',
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
  defaultPaymentCard: string = null;
  p2pOnSave: string = null;
  isP2PActive = new FormControl();
  cardForm = new FormGroup({
    cardNumber: new FormControl(),
    expirationMonth: new FormControl(),
    expirationYear: new FormControl(),
    cvv2Code: new FormControl(),
  });
  isNewCardAdded: boolean = false
  cardList: any = [];
  dontTouched: any[] = [];
  //{cashBoxId:string,
  // methodsArr:[{
  // method: function,
  // args: []
  // }]}

  cardListMethods: any = [];
  sortHelper = new SortHelper();
  tableSearch = new FormControl();
  errStatusMassage: string = null;


  ngOnInit(): void {
    this.activatedRoute.queryParamMap.subscribe((param) => {
      this.userId = +param.get('userId');
      this.getUserInfo();
      this.getCommissions();
      this.getCardList()
    });

    this.isEditMode = false;
    this.isP2PActive.valueChanges.subscribe(v => {
      if (v && !this.defaultPaymentCard) {
        this.addMerchantPaymentCard()
      } else if (this.defaultPaymentCard) {
        this.setMerchantP2p(v)
      }
    })
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
      this.getP2pInfo()

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

  getP2pInfo() {
    this.p2pService.getP2pInfo(this.userId).then(resp => {
      this.isP2PActive.patchValue(resp.data.p2pAllowed, {emitEvent: false})
      this.defaultPaymentCard = resp.data.cardNumber
    })
  }

  async getCommissions() {
    this.cardListMethods = []
    this.cashBoxList = new FormArray([])
    const data = {...await this.userService.getUsersCommissions(this.userId)}.data
    for (const cashBox of data) {
      this.p2pService.getCashBoxP2pInfo(cashBox['cashBoxId']).then(resp => {
        const p2pInfo = resp.data
        const form = new FormGroup({
          cashBoxId: new FormControl(cashBox['cashBoxId']),
          cashBoxName: new FormControl(cashBox['cashBoxName']),
          clientFee: new FormControl(cashBox['clientFee']),
          merchantFee: new FormControl(cashBox['merchantFee']),
          totalFee: new FormControl(cashBox['totalFee']),
          cardNumber: new FormControl(p2pInfo['cardNumber']),
          p2pAllowed: new FormControl(p2pInfo['p2pAllowed']),
          useDefaultCard: new FormControl(p2pInfo['useDefaultCard'])
        });
        this.cashBoxList.controls.push(form);
      })
    }
  }

  editUserData() {
    if(this.merchantInfoForm.invalid||this.userInfoForm.invalid){
      this.errStatusMassage='Заполните все необходимые поля';
      return;
    }
    if (this.userRoles.ROLE_MERCHANT) {
      this.merchantInfoForm.value.phone = this.merchantInfoForm.value.phone ? '+' + this.merchantInfoForm.value.phone : null;
      this.merchantInfoForm.value.mainphone = this.merchantInfoForm.value.mainphone ? '+' + this.merchantInfoForm.value.mainphone : null;
      this.kycService.setKycInfo(this.merchantInfoForm.value).then(resp => {
        this.getUserInfo()
      }).catch(err => {
        switch (err.status) {
          case 500: this.errStatusMassage = 'Ошибка сервера, попробуйте позже'; break;
          case 0: this.errStatusMassage = 'Отсутствие интернет соединения'; break;
          default: this.errStatusMassage = err.statusMessage; break;
        }
      })
    } else {
      this.userInfoForm.value.phone = '+' + this.userInfoForm.value.phone;
      this.userService.editUserData(this.userInfoForm.value).then(resp => {
        this.getUserInfo()
      }).catch(err => {
        switch (err.status) {
          case 500: this.errStatusMassage = 'Ошибка сервера, попробуйте позже'; break;
          case 0: this.errStatusMassage = 'Отсутствие интернет соединения'; break;
          default: this.errStatusMassage = err.statusMessage; break;
        }
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
    this.userRolesForm.controls[role].patchValue(!this.userRolesForm.controls[role].value)
  }

  saveCashBoxFee() {
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
    return this.userService.editUsersCommissions(data)
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

  cancelModal() {
    if (this.p2pOnSave === 'mainSettings' && !this.defaultPaymentCard) {
      this.isP2PActive.patchValue(false)
    }
    this.p2pOnSave = null;
    this.modalService.dismissAll(false)
  }

  onChangeCashBoxP2p(cashBox) {
    this.saveMethodsOnCashBoxBeforeSave(cashBox, 'setCashBoxP2p', {...cashBox.value})
  }

  setCashBoxP2p(cashBox, useDefaultCard = false) {
    let data = {
      'p2pAllowed': cashBox.p2pAllowed,
      'cashBoxId': cashBox.cashBoxId,
      'useDefaultCard': useDefaultCard ? useDefaultCard : cashBox.useDefaultCard
    }
    return this.p2pService.setP2pCashBox(data)
    //   .then(() => {
    //   this.getCommissions()
    // })
  }

  setMerchantP2p(value = null) {
    let data = {
      'p2pAllowed': value !== null ? value : this.isP2PActive.value,
      'merchantId': this.userId
    }
    this.p2pService.setP2p(data).then(() => {
      this.getP2pInfo()
      if (!value) {
        this.cashBoxList.controls.forEach(cashBox => {
          if (cashBox['value'].p2pAllowed) {
            cashBox['controls'].p2pAllowed.patchValue(false)
            this.saveMethodsOnCashBoxBeforeSave(cashBox, 'setCashBoxP2p', {...cashBox.value})
            // this.setCashBoxP2p(cashBox.value)
          }
        })
      } else {
        this.getCommissions()
      }
    })
  }

  saveMethodsOnCashBoxBeforeSave(cashBox, method, args = null) {
    const newMetod = {
      method: method,
      args: [args]
    }
    const cashBoxExistMethods = this.cardListMethods.filter(el => el.cashBoxId === cashBox.value.cashBoxId)[0]
    if (cashBoxExistMethods) {
      const existingMethod = cashBoxExistMethods.methodsArr.filter(el => el.method === method)[0]
      if (existingMethod) {
        existingMethod.args = [args];
      } else {
        cashBoxExistMethods.methodsArr.push({...newMetod})
      }
    } else {
      this.cardListMethods.push({
        cashBoxId: cashBox.value.cashBoxId,
        methodsArr: [{...newMetod}]
      })
    }
  }

  saveButton

  addCashBoxPaymentCard(cashBox) {
    this.paymentCard.open().then(card => {
      if (card.hasOwnProperty('token')) {
        this.saveMethodsOnCashBoxBeforeSave(cashBox, 'setCashBoxCard', [card.id, cashBox.value.cashBoxId])
        // this.setCashBoxCard(card.id, cashBox.value.cashBoxId).subscribe(() => {
        //   this.getCommissions()
        this.modalService.dismissAll(false)
        // })
      } else {
        this.registerPaymentCard(card.cardNumber, card.expirationYear, card.expirationMonth, card.cvv2Code)
          .subscribe(response => {
            this.getCardList()
            this.saveMethodsOnCashBoxBeforeSave(cashBox, 'setCashBoxCard', [response.data.cardId, cashBox.value.cashBoxId])
            // this.setCashBoxCard(response.data.cardId, cashBox.value.cashBoxId).subscribe(()=>{
            //   this.getCommissions()
            //   this.modalService.dismissAll(false)
            // })
            this.modalService.dismissAll(false)
          })
      }
      cashBox.controls.useDefaultCard.setValue(card.cardNumber === this.defaultPaymentCard)
      cashBox.controls.cardNumber.setValue(card.cardNumber)
      console.log(card.cardNumber === this.defaultPaymentCard);
    })
  }

  addMerchantPaymentCard() {
    this.paymentCard.open().then(modalResult => {
        if (modalResult.hasOwnProperty('token')) {
          this.setDefaultCard(modalResult.id)
        } else if (modalResult) {
          this.registerPaymentCard(modalResult.cardNumber, modalResult.expirationYear, modalResult.expirationMonth, modalResult.cvv2Code)
            .subscribe(response => {
              if (response) {
                this.isP2PActive ? this.setDefaultCard(response['data'].cardId) : this.setMerchantP2p()
              }
              this.paymentCard.close();
              this.getCardList()
            })
        } else {
          this.isP2PActive.setValue(false, {emitEvent:false})
          this.modalService.dismissAll(false)
        }
      }
    )
  }

  registerPaymentCard(cardNumber, expirationYear, expirationMonth, cvv2Code) {
    return this.p2pService.registerCard(cardNumber.trim().replaceAll(' ', ''), expirationYear, expirationMonth, cvv2Code, this.userId).pipe(switchMap(resp => {
      return this.p2pService.cardCheckValidity(resp.data.id)
    }))
  }

  setDefaultCard(cardId) {
    const data = {
      'merchantId': this.userId,
      'cardId': cardId
    }
    this.p2pService.setDefaultCard(data).subscribe(() => {
      this.getP2pInfo()
      this.modalService.dismissAll(false)
    })
  }

  setCashBoxCard(cardId, cashBoxId) {
    const data = {
      'cashBoxId': cashBoxId,
      'merchantId': this.userId,
      'cardId': cardId
    }
    return this.p2pService.setCashBoxCard(data)
  }

  async getCardList() {
    this.cardList = {...await this.p2pService.clientCardList(this.userId)}.data;
    this.dontTouched = {...await this.p2pService.clientCardList(this.userId)}.data;
  }

  logData(data1, data2) {
    console.log(data1);
    console.log(data2);
    console.log(data1 === data2);
  }

  onUseDefaultCard(cashBox) {
    if (!cashBox.value.useDefaultCard) {
      cashBox.controls.cardNumber.reset();
    } else {
      cashBox.controls.cardNumber.setValue(this.defaultPaymentCard)
      this.saveMethodsOnCashBoxBeforeSave(cashBox, 'setCashBoxP2p', {...cashBox.value})
      // this.setCashBoxP2p(cashBox.value,true)
    }

  }

  saveCashBoxSettings(cashBox) {
    //{cashBoxId:string,
    // methodsArr:[{
    // method: function,
    // args: []
    // }]}
    this.cardListMethods.forEach(el => {
      if (el.cashBoxId === cashBox.cashBoxId) {
        const promises = []
        el.methodsArr.forEach(method => {
          console.log(method.args);
          promises.push(this[method.method](...method.args))
        })
        promises.push(this.saveCashBoxFee())
        Promise.all(promises).then(() => {
          this.getCommissions();
        })
      }
    })
  }

  nextSort(field) {
    let sh: SortHelper = this.sortHelper;
    this.sortHelper = sh.nextSort(field);
  }

  get sortedActions() {
    if (this.sortHelper.sort.sortBy === null) {
      this.cardList = this.searchInputService.filterData(this.dontTouched, this.tableSearch.value);
      return this.cardList
    } else {
      let sorted = this.dontTouched.sort(
        (a, b) => {
          let aField = a[this.sortHelper.sort.sortBy];
          let bField = b[this.sortHelper.sort.sortBy];
          let res = aField == bField ? 0 : (aField > bField ? 1 : -1);
          return this.sortHelper.sort.increase ? res : -res;
        }
      );
      sorted = this.searchInputService.filterData(sorted, '');
      this.cardList = [...sorted];
      return this.cardList
    }
  }

  deleteCard(card: any) {
    this.modalType = this.modalTypes['deleteCardConfirm']
    this.massageModal.open().then(() => {
      this.p2pService.deleteMerchantCard(card.id, this.userId).then(() => {
        this.getCardList()
      })
    })
  }
}
