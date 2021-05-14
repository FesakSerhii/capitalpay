import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {AuthService} from '../service/auth.service';
import {Router} from '@angular/router';
import {HttpResponse} from "@angular/common/http";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  constructor(public authService:AuthService,private router: Router) { }
  loginForm = new FormGroup({
    email: new FormControl('+37096384345'),
    password: new FormControl('blablabla'),
    isIpTrusted: new FormControl()
  });
  toggle = {
    showPass : false,
    showConfirmPass: false
  };
  ngOnInit(): void {
  }
  loginAction(){
    this.authService.login(this.loginForm.value.email,this.loginForm.value.password).then((resp: HttpResponse<any>)=>{
      sessionStorage.setItem('token',resp.headers.get('Authorization').replace('Bearer','').trim());
      this.router.navigate(['/admin-panel/dashboard']);
    })
  }
}
