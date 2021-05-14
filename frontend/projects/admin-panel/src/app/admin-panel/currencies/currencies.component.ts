import { Component, OnInit } from '@angular/core';
import {CurrencyService} from '../../service/currency.service';
import {FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-currencies',
  templateUrl: './currencies.component.html',
  styleUrls: ['./currencies.component.scss']
})
export class CurrenciesComponent implements OnInit {

  constructor() { }
  ngOnInit(): void {
  }

}
