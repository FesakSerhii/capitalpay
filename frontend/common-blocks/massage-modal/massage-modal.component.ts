import {Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";
import {MassageServiceService} from "../../src/app/service/massage-service.service";

@Component({
  selector: 'app-massage-modal',
  templateUrl: './massage-modal.component.html',
  styleUrls: ['./massage-modal.component.scss']
})
export class MassageModalComponent implements OnInit {

  constructor(public modalService: NgbModal) { }

  @Input() messageType:string;
  @ViewChild("massageModalContent", {static: false}) massageModal: TemplateRef<any>;
  @Output() onModalClose = new EventEmitter<any>();

  massages : any = {
    saveMassage: "Данные сохранены.",
    messageSend: "Сообщение успешно отправлено",
    invalidDateMassage: "Дата окончания периода не может быть раньше даты начала периода.",
    deleteConfirmation: "Подтвердите удаление",
    userDeleteConfirmation:"Вы действительно хотите удалить пользователя",
    cardDeleteConfirmation:"Вы действительно хотите удалить карту",
    userBlockConfirmation:"Вы действительно хотите заблокировать пользователя",
    userDeleteSuccessful:"Пользватель успешно удален",
    userBlockSuccessful:"Пользватель успешно заблокирован",
    userActivateSuccessful:"Пользватель успешно активирован",
    cardNumberInvalid:"Введенная карта невалидна",
    req500Error:"Ошибка сервера",
    req400Error:"Ошибка запроса",
    noInternetError:"Отсутствует интернет соединение",
  };
  private modal: NgbModalRef = null;

  ngOnInit() {

  }

  open():Promise<any>{
    if(!this.modalService.hasOpenModals()){
      this.modal = this.modalService.open(this.massageModal);
      return this.modal.result;
    };
  }
  openWithMassageType(massageType){
    this.messageType = massageType;
    this.open()
  }

  close() {
    this.modal.close(true);
    this.onModalClose.emit(true);
    this.modal.dismiss()
  }

  // subscribeToInterceptor(){
  //   this.massageServiceService.newMassagesSubscription$.subscribe(
  //     massage => {
  //       this.openWithMassageType(massage);
  //     });
  // }
}
