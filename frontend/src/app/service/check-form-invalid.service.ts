import { Injectable } from '@angular/core';
import {Form, FormControl, FormGroup} from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class CheckFormInvalidService {

  constructor() { }

  isInvalid(form: FormGroup|FormControl, field: string='') {
    if (form instanceof FormGroup){
      return field===''?form.invalid && form.touched:form.controls[field] && form.controls[field].invalid && form.controls[field].touched
    }else {
      return form.invalid && form.touched
    }
  }
}
