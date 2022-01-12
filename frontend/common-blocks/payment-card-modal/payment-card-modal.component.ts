import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {P2pService} from '../../projects/admin-panel/src/app/service/p2p.service';
import {ActivatedRoute} from '@angular/router';
import {validateCard} from '../validators/paymentCardNumberValidator';
import {expirationDateValidator} from '../validators/dateCompareValidator';


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

  @ViewChild('paymentCardModal', {static: false}) paymentCardModalRef: TemplateRef<any>;
  // @Output() onModalClose = new EventEmitter<any>();
  @Input() defaultPaymentCard: string = null;
  isAddNew: boolean = false


  private modal: NgbModalRef = null;
  cardForm = new FormGroup({
    cardNumber: new FormControl('',[validateCard()]),
    expirationMonth: new FormControl(),
    expirationYear: new FormControl(),
    cvv2Code: new FormControl(),
  });
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

  async ngOnInit() {
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

  open(): Promise<any> {
   return this.getCardList().then(()=>{
      this.modal = this.modalService.open(this.paymentCardModalRef,{backdrop:false});
      return this.modal.result;
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
}
