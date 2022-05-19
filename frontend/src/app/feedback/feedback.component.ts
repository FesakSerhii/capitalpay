import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {SupportService} from '../service/support.service';
import {Router} from '@angular/router';
import {CheckFormInvalidService} from "../service/check-form-invalid.service";

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.scss']
})
export class FeedbackComponent implements OnInit {

  constructor(private supportService:SupportService,public checkFormInvalidService:CheckFormInvalidService, private router:Router) { }
feedbackForm = new FormGroup({
  companyName: new FormControl('',[Validators.required]),
  name: new FormControl('',[Validators.required]),
  phone: new FormControl('',[Validators.required]),
  email: new FormControl('',[Validators.required]),
  text: new FormControl('',[Validators.required])
})

  ngOnInit(): void {
  }

  sendFeedback() {
    this.supportService.sendSupportRequest(this.feedbackForm.value).then(()=>{
      this.router.navigate(['/page']);
    })
  }
  isInvalid(form: FormGroup|FormControl,field: string='') {
    return this.checkFormInvalidService.isInvalid(form,field);
  }
}
