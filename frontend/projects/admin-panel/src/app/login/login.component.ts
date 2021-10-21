import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {AuthService} from '../service/auth.service';
import {Router} from '@angular/router';
import {HttpResponse} from "@angular/common/http";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  constructor(public authService:AuthService,public modalService: NgbModal,private router: Router) { }
  loginForm = new FormGroup({
    email: new FormControl('+37096384345'),
    password: new FormControl('blablabla'),
    isIpTrusted: new FormControl()
  });
  toggle = {
    showPass : false,
    showConfirmPass: false
  };
  isTwoFactor:boolean = false;
  sms = new FormControl();

  ngOnInit(): void {
  }
  loginAction(){
    this.authService.login(this.loginForm.value.email,this.loginForm.value.password).then((resp: HttpResponse<any>)=>{
      console.log(resp);
      if(resp.body.data=== "SMS sent"){
        this.isTwoFactor = true;
      }else{
        sessionStorage.setItem('token',resp.headers.get('Authorization').replace('Bearer','').trim());
        this.router.navigate(['/admin-panel/dashboard']);
      }
    })
  }
  confirmCode() {
    this.authService.login(this.loginForm.value.email,this.loginForm.value.password,this.sms.value).then(resp=>{
      sessionStorage.setItem('token',resp.headers.get('Authorization').replace('Bearer','').trim());
      this.router.navigate(['/admin-panel/dashboard']);
    })
  }
}
