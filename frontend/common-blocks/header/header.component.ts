import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
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
  constructor(public modalService: NgbModal,public authService:AuthService,private router: Router,) { }
  loginForm = new FormGroup({
    email: new FormControl('+38095384343'),
    password: new FormControl('heXog0naL5'),
    isIpTrusted: new FormControl()
  });
  registerForm = new FormGroup({
    username: new FormControl(),
    phone: new FormControl(),
    email: new FormControl(),
    comment: new FormControl(),
  })

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
      this.modalService.dismissAll();
      this.router.navigate(['/merchant/transactions-log']);
    })
  }
}
