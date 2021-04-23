import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {AuthService} from '../service/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  constructor(public authService:AuthService,private router: Router) { }
  loginForm = new FormGroup({
    email: new FormControl('+38095384343'),
    password: new FormControl('heXog0naL5'),
    isIpTrusted: new FormControl()
  });
  toggle = {
    showPass : false,
    showConfirmPass: false
  };
  ngOnInit(): void {
  }
  loginAction(){
    this.authService.login(this.loginForm.value.email,this.loginForm.value.password).then(resp=>{
      this.router.navigate(['/admin-panel/dashboard']);
    })
  }
}
