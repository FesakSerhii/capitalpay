import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {FormControl, FormGroup} from '@angular/forms';
import {P2pService} from '../../projects/admin-panel/src/app/service/p2p.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-payment-card-modal',
  templateUrl: './payment-card-modal.component.html',
  styleUrls: ['./payment-card-modal.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentCardModalComponent implements OnInit {

  constructor(public modalService: NgbModal,private p2pService: P2pService,private activatedRoute: ActivatedRoute,) {
  }

  @ViewChild('paymentCardModal', {static: false}) paymentCardModalRef: TemplateRef<any>;
  @Output() onModalClose = new EventEmitter<any>();
  isAddNew: boolean = false

  private modal: NgbModalRef = null;
  cardForm = new FormGroup({
    cardNumber: new FormControl(),
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
    this.getCardList()
  }
  async getCardList(){
    this.cardList= {...await this.p2pService.clientCardList(this.userId)}.data
    this.cardListTitles= this.cardList.map(el=>{return {title:el.cardNumber,value:el.id}})
    this.isAddNew = !(this.cardList.length >= 2);
  }

  open(): Promise<any> {
   return this.getCardList().then(()=>{
      this.modal = this.modalService.open(this.paymentCardModalRef);
      return this.modal.result;
    })

  }
  close() {
    this.modal.close(true);
    this.onModalClose.emit(true);
    this.cardForm.reset()
  }

  sendResult() {
    if(this.isAddNew){
      this.modal.close(this.cardForm.value);
      this.cardForm.reset()
    }else{
      this.modal.close(this.cardList.filter(el=>el.id===this.selectedCard.value)[0]);
      this.cardForm.reset()
    }
  }
}
