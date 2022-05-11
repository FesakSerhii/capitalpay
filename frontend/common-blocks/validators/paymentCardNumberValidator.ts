import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';
const valid = require("card-validator");

export function validateCard(): ValidatorFn {
  return (control:AbstractControl) : ValidationErrors | null => {
    const isCardValid = valid.number(control.value)
    return !isCardValid.isPotentiallyValid ? {'invalid card number': true}: null;
  }
}
