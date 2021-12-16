import {Component, EventEmitter, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-payment-card-modal',
  templateUrl: './payment-card-modal.component.html',
  styleUrls: ['./payment-card-modal.component.scss']
})
export class PaymentCardModalComponent implements OnInit {

  constructor(public modalService: NgbModal) { }

  @ViewChild("paymentCardModal", {static: false}) paymentCardModalRef: TemplateRef<any>;
  @Output() onModalClose = new EventEmitter<any>();

  private modal: NgbModalRef = null;
  cardForm = new FormGroup({
    cardNumber: new FormControl(),
    expirationMonth: new FormControl(),
    expirationYear: new FormControl(),
    cvv2Code: new FormControl(),
  });

  ngOnInit(): void {
  }
  open():Promise<any>{
    this.modal = this.modalService.open(this.paymentCardModalRef);
    return this.modal.result;
  }
  close() {
    this.modal.close(true);
    this.onModalClose.emit(true);
  }
  sendResult() {
    this.modal.close(this.cardForm.value);
  }
}
