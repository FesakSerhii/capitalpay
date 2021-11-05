import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-confirm-action-modal',
  templateUrl: './confirm-action-modal.component.html',
  styleUrls: ['./confirm-action-modal.component.scss']
})
export class ConfirmActionModalComponent implements OnInit {

  @Input() messageType:string;
  @ViewChild("massageModalContent", {static: false}) massageModal: TemplateRef<any>;
  constructor(public modalService: NgbModal) { }
  massages : any = {
    logOut: "Вы уверены что хотите выйти?",
    changePassword: "Вы уверены что хотите изменить пароль?",
    save: "Вы уверены что хотите сохранить изменения?",
    deleteCashBoxConfirmation: "Вы действительно хотите удалить кассу?",
    deleteLayout: "Вы действительно хотите удалить страницу?",
  };
  private modal: NgbModalRef = null;

  ngOnInit(): void {
  }

  open():Promise<any>{
    this.modal = this.modalService.open(this.massageModal);
    return this.modal.result;
  }
}
