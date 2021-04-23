import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-transactions-log',
  templateUrl: './transactions-log.component.html',
  styleUrls: ['./transactions-log.component.scss']
})
export class TransactionsLogComponent implements OnInit {

  constructor() { }
  activeTab = 'tab1';

  ngOnInit(): void {
  }

}
