import {AbstractControl, FormGroup} from "@angular/forms";
import * as moment from "moment";

export function dateCompareValidator(formGroup: FormGroup, otherField, reverse = false) {
    return (control: AbstractControl): {[key: string]: any} | null => {
        let value = control.value;
        let otherValue = formGroup.get(otherField).value;
        if (!value || !otherValue) {
            return null;
        }

        let date1 = parseInt(moment(`${value.year}-${value.month}-${value.day}`).format("X"));
        let date2 = parseInt(moment(`${otherValue.year}-${otherValue.month}-${otherValue.day}`).format("X"));
        if (!reverse) {
            return date2 < date1 ? {"dateCompare": "invalid date"} : null;
        }
        return date1 < date2 ? {"dateCompare": "invalid date"} : null;
    }
}
export function expirationDateValidator(formGroup: FormGroup, yearControl,reverse=false) {
  return (monthControl: AbstractControl): {[key: string]: any} | null => {
    let month = !reverse?monthControl.value:formGroup.get(yearControl).value;
    let year = !reverse?formGroup.get(yearControl).value:monthControl.value;

    if (!month || !year) {
      return null;
    }

    let cardExp = parseInt(moment(`${year}-${month}-01`).format("X"));
    let today = parseInt(moment(`${new Date().getFullYear()}-${new Date().getMonth()+1}-${new Date().getDate()}`).format("X"));
    if (!reverse) {
      return cardExp > today ? {"expirationDateValidator": "invalid exp date"} : null;
    }
    return today > cardExp ? {"expirationDateValidator": "invalid exp date"} : null;
  }
}
