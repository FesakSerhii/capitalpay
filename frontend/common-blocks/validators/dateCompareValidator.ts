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
