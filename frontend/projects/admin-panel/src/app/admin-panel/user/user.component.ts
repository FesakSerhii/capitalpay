import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {UserService} from '../../service/user.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Router} from '@angular/router';
import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  @ViewChild('addNewUser', {static: false}) addNewUserModal: TemplateRef<any>;
  constructor(private userService: UserService,private modalService: NgbModal, private router: Router) { }
  userList:[any]=null;
  roleList ={
    ROLE_USER:'Пользователь',
    ROLE_OPERATOR:'Оператор',
    ROLE_ADMIN:'Админ',
    ROLE_MERCHANT:'Мерчант'
  };
  newUserForm = new FormGroup({
    password:new FormControl('',Validators.required),
    email:new FormControl('',[Validators.email]),
    phone:new FormControl('',[Validators.required,Validators.minLength(11),Validators.maxLength(11)]),
    roleList: new FormArray([new FormControl(Validators.required)])
  });
  filter = new FormGroup({
    role: new FormControl()
  })
  confirmPassword= new FormControl();
  possibleRoles:[any]=null;
  possibleRolesList:[any]=null;
  ngOnInit(): void {
    this.getUsers();
    this.getPossibleRoles();
  }
  getPossibleRoles(){
    this.userService.getUserRolesList().then(resp=>{
      this.possibleRolesList = resp.data;
      this.possibleRoles = resp.data.map(el=>{return {title:this.roleList[el.authority],value:el.id}});
    })
  }
  getUsers(){
    this.userService.getUserList().then(resp=>{
      this.userList = resp.data;
      this.userList.map((el)=> {
        let role = '';
        for(const userRole of el.roles){
          role = role===''? role+this.roleList[userRole.authority]:role+'/'+this.roleList[userRole.authority]
        }
        el.roles = role;
      })
      // active: true
      // blocked: false
      // email: "arsenguzhva@gmail.com"
      // id: 12
      // password: null
      // realname: null
      // roles: [{id: 10, authority: "ROLE_ADMIN"}, {id: 11, authority: "ROLE_USER"}]
      // username: "+38095384343"
    })
  }
  addForm(form) {
    form.push(new FormControl(''));
  }
  deleteLastForm(form) {
    form.removeAt(form.controls.length - 1)
  }
  open(){
    this.modalService.open(this.addNewUserModal,{windowClass:''});
  }
  close(){
    console.log(this.newUserForm.value.roleList);
    this.modalService.dismissAll()
  }
  navigateToSettings(id){
    this.router.navigate(['/admin-panel/user/settings'],{queryParams: {
        userId: id,
      },
      queryParamsHandling: "merge"})
  }
  createUser(){
    // tslint:disable-next-line:no-unused-expression
    // let user = {}
    // Object.assign(user,this.newUserForm.value);
    for(let role in this.newUserForm.value['roleList']){
      this.newUserForm.value['roleList'][role]=this.possibleRolesList.filter(el => el.id === this.newUserForm.value['roleList'][role])[0].authority;
    }
    this.newUserForm.value.phone ='+'+this.newUserForm.value.phone;
    this.userService.createUser(this.newUserForm.value).then(resp=>{
      this.getUsers();
    })
  }
  comparePasswords(){
   return this.newUserForm.value.password === this.confirmPassword.value
  }
}
