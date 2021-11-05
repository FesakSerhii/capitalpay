import {Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {FormControl, FormGroup} from '@angular/forms';
import {AuthService} from '../../src/app/service/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  @ViewChild('login', {static: false}) loginModal: TemplateRef<any> | undefined;
  @ViewChild('register', {static: false}) registerModal: TemplateRef<any> | undefined;
  @Input() theme: string = 'white';
  constructor(public modalService: NgbModal,public authService:AuthService,private router: Router,) { }
  loginForm = new FormGroup({
    email: new FormControl('+37096384345'),
    password: new FormControl('blablabla'),
    isIpTrusted: new FormControl(false)
  });
  registerForm = new FormGroup({
    username: new FormControl(),
    email: new FormControl(),
    comment: new FormControl(),
  })
  toggle = {
    showPass : false,
    showConfirmPass: false
  };
  isTwoFactor:boolean = false;
  sms = new FormControl();

  ngOnInit(): void {
  }
  open(modal: any){
    this.modalService.open(modal)
  }

  switchToLogin(modal: any) {
    this.modalService.dismissAll();
    this.open(modal)
  }
  loginAction(){
    this.authService.login(this.loginForm.value.email,this.loginForm.value.password).then(resp=>{
      if(resp.body['data']=== "SMS sent"){
        this.isTwoFactor = true;
      }else{
        sessionStorage.setItem('token',resp.headers.get('Authorization').replace('Bearer','').trim());
        this.modalService.dismissAll();
        this.router.navigate(['/merchant/transaction-log']);
      }
    })
  }

  confirmCode() {
    this.authService.login(this.loginForm.value.email,this.loginForm.value.password,this.sms.value).then(resp=>{
      sessionStorage.setItem('token',resp.headers.get('Authorization').replace('Bearer','').trim());
      this.modalService.dismissAll();
      this.router.navigate(['/merchant/transaction-log']);
    })
  }
}
