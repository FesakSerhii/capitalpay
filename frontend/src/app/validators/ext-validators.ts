import {AbstractControl, Validators} from "@angular/forms";
import {ValidatorFn} from "@angular/forms";
import * as moment from "moment";

export class ExtValidators {
    static date(control: AbstractControl) {
        let value = control.value;
        if (!value || !value.length) {
            return null;
        }
        if (!/^\d{2}\.\d{2}\.\d{4}$/.test(value)) {
            return {"invalidDate": true};
        }
        let dateComponents: string[] = value.split(".");
        if (isNaN(Date.parse(`${dateComponents[2]}-${dateComponents[1]}-${dateComponents[0]}`))) {
            return {invalidDate: true};
        }

        return null;
    }

    static IIN(control: AbstractControl) {
        let value = control.value;
        if (!value || !value.length) {
            return null;
        }
        if (!/^\d{12}$/.test(value)) {
            return {invalidIIN: true};
        }


        let controlNumber = value.split("").slice(0, 11).map((d, i) => (i + 1) * parseInt(d)).reduce((a , v) => a + v) % 11;
        if (controlNumber === 10) {
            let sequence = value.split("").slice(9, 11);
            sequence.push(...value.split("").slice(0, 9));
            controlNumber = sequence.map((d, i) => (i + 1) * parseInt(d)).reduce((a , v) => a + v) % 11;
        }
        if (`${controlNumber}` !== value[11]) {
            return {invalidIIN: true};
        }

        return null;
    }

    static requiredIf(condition: (parent) => boolean) {
        return (control: AbstractControl) => {
            if (!control.parent) {
                return null;
            }
            if (condition(control.parent)) {
                return Validators.required(control);
            }
            return null;
        }
    }

    static validateNotEmpty(validators: ValidatorFn[]) {
        return (control: AbstractControl) => {
            let value = control.value;
            if (!value || !value.length) {
                return null;
            }
            let res: any = {};
            for (let f of validators) {
                Object.assign(res, f(control));
            }
            return Object.keys(res).length > 0 ? res: null;
        }
    }

    static kazCyr(control: AbstractControl) {
        let value = control.value;
        if (!value || !value.length) {
            return null;
        }

        if (!/^[\u0400-\u04FF][\u0400-\u04FF\u00A0\u0020\u002D\u2013\u2212]*$/.test(value)) {
            return {invalidCharacter: true}
        }
        return null;
    }

    static noSeveralSpaces(control) {
        let value = control.value;
        if (!value || !value.length) {
            return null;
        }

        if (/[\u00A0\u0020][\u00A0\u0020]/.test(value)) {
            return {severalSpaces: true};
        }
        return null;
    }

    static latinName(control: AbstractControl) {
        let value = control.value;
        if (!value || !value.length) {
            return null;
        }
        return (Validators.pattern(/^[A-z][A-z\s-']+$/))(control)
    }

    static noFeatureDates(control) {
        if (!control.value) {
            return null;
        }
        const now = moment().unix();
        if (control.value.unix() > now) {
            return {noFeature: true};
        }
        return true;
    }

    static dateCompareValidator(otherField, reverse = false) {
        return (control: AbstractControl): {[key: string]: any} | null => {
            if (!control.parent) {
                return null;
            }
            let value = control.value;
            let otherValue = control.parent.get(otherField).value;
            if (!value || !otherValue) {
                return null;
            }

            let date1 = value.unix();
            let date2 = otherValue.unix();
            if (!reverse) {
                return date2 < date1 ? {"dateCompare": "invalid date"} : null;
            }
            return date1 < date2 ? {"dateCompare": "invalid date"} : null;
        }
    }

    static dateAfter(date) {
        return (control) => {
            let dateAfter = moment(date).unix();
            if (!control.value) {
                return null;
            }
            if (control.value.unix() < dateAfter) {
                return {dateAfter: true};
            }
            return null;
        }
    }
}
