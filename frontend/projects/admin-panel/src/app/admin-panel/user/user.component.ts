import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {UserService} from '../../service/user.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  @ViewChild('addNewUser', {static: false}) addNewUserModal: TemplateRef<any>;
  constructor(private userService: UserService,private modalService: NgbModal) { }

  ngOnInit(): void {
    this.userService.getUserList().then(resp=>{
      console.log(resp);
    })
  }
  open(){
    this.modalService.open(this.addNewUserModal,{windowClass:''});
  }
  close(){
    this.modalService.dismissAll()
  }

}
