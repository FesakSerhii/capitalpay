import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {P2pService} from '../../projects/admin-panel/src/app/service/p2p.service';
import {ActivatedRoute} from '@angular/router';
import {validateCard} from '../validators/paymentCardNumberValidator';
import {expirationDateValidator} from '../validators/dateCompareValidator';
import {environment} from '../../projects/admin-panel/src/environments/environment';

@Component({
  selector: 'app-payment-card-modal',
  templateUrl: './payment-card-modal.component.html',
  styleUrls: ['./payment-card-modal.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentCardModalComponent implements OnInit {
  expInvalid: boolean=false;

  constructor(public modalService: NgbModal,private p2pService: P2pService,private activatedRoute: ActivatedRoute,) {
  }
  @ViewChild('form') form: ElementRef;
  @ViewChild('paymentCardModal', {static: false}) paymentCardModalRef: TemplateRef<any>;
  // @Output() onModalClose = new EventEmitter<any>();
  @Input() defaultPaymentCard: string = null;
  isAddNew: boolean = false;
  blockAddingNewCardManually: boolean = environment['blockAddingNewCardManually'];


  private modal: NgbModalRef = null;
  cardForm = new FormGroup({
    cardNumber: new FormControl('',[validateCard()]),
    expirationMonth: new FormControl(),
    expirationYear: new FormControl(),
    cvv2Code: new FormControl(),
  });
  redirectForm = new FormGroup({
    xml: new FormControl(),
    backLink: new FormControl(),
    postLink: new FormControl(),
    FailureBackLink: new FormControl(),
  });
  action: string ='';
  selectedCard = new FormControl();
  cardListTitles:any=null;
  cardList: [
    {
      'id': string,
      'token': string,
      'cardNumber': string,
      'valid': boolean
    },
  ];
  userId: number;
  yearsArr: number[] = [];
  cashBox: any;

  async ngOnInit() {
    let currentYear = new Date().getFullYear();
    for (let i = 0; i < 6; i++) {
      this.yearsArr.push(currentYear);
      currentYear++
    }
    this.activatedRoute.queryParamMap.subscribe((param) => {
      this.userId = +param.get('userId');
    })
    let dateFields = {
      expirationMonth: "expirationYear",
    };
    for(let v in dateFields){
      this.cardForm.get(v).setValidators([expirationDateValidator(this.cardForm, dateFields[v])]);
      this.cardForm.get(dateFields[v]).setValidators([expirationDateValidator(this.cardForm, v, true)]);
    }
    this.getCardList()
  }
  async getCardList(){
    this.cardList= {...await this.p2pService.clientCardList(this.userId)}.data
    this.cardListTitles = this.cardList.map(el=>{return {title:el.cardNumber,value:el.id}})
    this.cardListTitles = this.cardListTitles.map(el=>{
      if(el['title']===this.defaultPaymentCard){
        el['title']=`${el['title']} карта по умолчанию`
      }
      return el;
    })
    this.isAddNew = !(this.cardList.length >= 2);
  }

  open(cashBox=null): Promise<any> {
    console.log(cashBox);
    this.cashBox = cashBox;
   return this.getCardList().then(()=>{
     if(this.blockAddingNewCardManually&&this.isAddNew){
       this.addNewCard()
     }else{
      this.modal = this.modalService.open(this.paymentCardModalRef,{backdrop:false});
      return this.modal.result;
     }
    })
  }
  close() {
    this.cardForm.reset()
    this.selectedCard.reset()
    this.modal.close(false);
  }

  sendResult() {
    if(this.isAddNew){
      this.modal.close(this.cardForm.value);
      this.cardForm.reset()
      this.selectedCard.reset()
    }else{
      this.modal.close(this.cardList.filter(el=>el.id===this.selectedCard.value)[0]);
      this.cardForm.reset()
      this.selectedCard.reset()
    }
  }

  dateInvalid() {
    this.expInvalid = false;
    if(this.cardForm.get('expirationMonth').value!==null&&this.cardForm.get('expirationYear').value!==null){
      setTimeout(() => {
        if (this.cardForm.get('expirationMonth').invalid || this.cardForm.get('expirationYear').invalid) {
          this.expInvalid = true;
          this.cardForm.get('expirationMonth').reset();
          this.cardForm.get('expirationYear').reset();
        }
      }, 200);
    }
  }
  addNewCard() {
    this[environment['cardRegisterFn']]();
  }
  registerPaymentCard(){
    this.isAddNew=true;
  }
  // registerPaymentCardWithBank() {
  //   this.modal.close(this.cashBox);
  //   this.cashBox = null;
  // }
  registerPaymentCardWithBank(cashBox=null) {
    if(cashBox){
      sessionStorage.setItem('user-settings', JSON.stringify({
        cashBox: cashBox,
        userId: this.userId
      }))
    }else{
      sessionStorage.setItem('user-settings', JSON.stringify({
        userId: this.userId
      }))
    }
    // return this.p2pService.registerCardWithBank(this.userId,this.cashBox).then(resp=>{
    //   this.redirectForm.patchValue(resp.data)
    //   console.log(this.redirectForm.value);
    //   setTimeout(() => this.form.nativeElement.submit(), 200);
    // });
       this.p2pService.registerCardWithBank(this.userId,this.cashBox).then(resp=>{
        this.redirectForm.patchValue(resp.data)
         this.form.nativeElement.action = resp.data.action;
         this.form.nativeElement.submit()
       });
  }
}
