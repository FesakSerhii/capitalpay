import {Component, ElementRef, forwardRef, HostListener, Input, OnInit, ViewChild} from '@angular/core';
import {ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR} from '@angular/forms';

@Component({
  selector: 'app-custom-select',
  templateUrl: './custom-select.component.html',
  styleUrls: ['./custom-select.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CustomSelectComponent),
      multi: true
    }
  ]
})
export class CustomSelectComponent implements OnInit, ControlValueAccessor {
  @Input() values: any[];
  @Input() classes = '';
  @Input() disabled = false;
  opened = false;
  private _value: any=null;
  @Input() placeholder = '';
  @Input() hasSearch = false;

  searchStr = new FormControl('');

  @ViewChild('selectElem', {static: false}) selectElement: ElementRef<any>;


  get filteredValues() {
    return !this.values ? []: this.values.filter((v) => {
      if (!this.hasSearch || this.searchStr.value.trim() === '') {
        return true;
      }
      return this.getValueTitle(this.getValueValue(v)).toLowerCase().indexOf(this.searchStr.value.trim().toLowerCase()) !== -1;
    })
  }

  get isDirectionTop() {
    if (!this.selectElement) {
      return false;
    }
    const element: any = this.selectElement.nativeElement;
    return window.innerHeight - element.getBoundingClientRect().bottom < 300;
  }

  get value() {
    return this._value;
  }

  onChange = (rating: number) => {};
  onTouched = () => {};

  constructor(private elementRef: ElementRef) {
  }

  ngOnInit() {

  }
  isDisabled(){
    this.disabled=true
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }
  getCurrentValuesText() {
    if (!Array.isArray(this._value) || this._value.length === 0) {
      return this.placeholder;
    }
    const texts = (Array.isArray(this._value) ? this._value: []).map((value) => this.getValueTitle(value));
    return texts.join(', ');
  }
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  writeValue(obj: any): void {
    this._value = obj;

  }

  getValueTitle(val) {
    if (val === null) {
      return null;
    }
    for (const v of this.values) {
      if (typeof v === 'object' && v !== null) {
        if (v.value === val) {
          return v.title;
        }
      } else if (v === val) {
        return val;
      }
    }
    return '';
  }

  getValueValue(val) {
    if (val === null) {
      return null;
    }
    return typeof val === 'object' ? val.value: val;
  }

  updateValue(v: any) {
    this.writeValue(this.getValueValue(v));
    this.onChange(this._value);
    this.opened = false;
  }

  @HostListener('document:click', ['$event'])
  clickOut(event) {
    if(!this.elementRef.nativeElement.contains(event.target)) {
      this.opened = false;
    }
  }
}
