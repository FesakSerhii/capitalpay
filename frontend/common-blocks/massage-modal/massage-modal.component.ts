import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-massage-modal',
  templateUrl: './massage-modal.component.html',
  styleUrls: ['./massage-modal.component.scss']
})
export class MassageModalComponent implements OnInit {

  constructor(public modalService: NgbModal) { }

  @Input() messageType:string;
  @ViewChild("massageModalContent", {static: false}) massageModal: TemplateRef<any>;

  massages : any = {
    saveMassage: "Данные сохранены.",
    messageSend: "Сообщение успешно отправлено",
    invalidDateMassage: "Дата окончания периода не может быть раньше даты начала периода.",
    deleteConfirmation: "Подтвердите удаление",
    userDeleteConfirmation:"Вы действительно хотите удалить пользователя",
    userBlockConfirmation:"Вы действительно хотите заблокировать пользователя",
    userDeleteSuccessful:"Пользватель успешно удален",
    userBlockSuccessful:"Пользватель успешно заблокирован",
    userActivateSuccessful:"Пользватель успешно активирован",
  };
  private modal: NgbModalRef = null;

  ngOnInit() {
  }
  open():Promise<any>{
    this.modal = this.modalService.open(this.massageModal);
    return this.modal.result;
  }
}
