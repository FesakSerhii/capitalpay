import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PaymentService} from "../../../service/payment.service";
import {SortHelper} from "../../../helper/sort-helper";

@Component({
  selector: 'app-payment-links-history',
  templateUrl: './payment-links-history.component.html',
  styleUrls: ['./payment-links-history.component.scss']
})
export class PaymentLinksHistoryComponent implements OnInit {
  merchantId: number = null;
  cashBoxId: any = null;
  cashBoxListHistory: any = null;
  visible: boolean;
  currentPage: number;
  sortHelper = new SortHelper();
  historyDetails:any = null;
  renewSuccess:boolean = false;

  constructor(private route: ActivatedRoute, private paymentService: PaymentService) { }

  ngOnInit(): void {
    this.cashBoxId = this.route.snapshot.paramMap.get('shop-id');
    this.paymentService.getPaymentLinkList().then(rest => {
      this.cashBoxListHistory = rest.data.filter(a => a.cashBoxId === Number(this.cashBoxId)).reverse();
      this.visible = true;
      this.currentPage = 1
    })
  }

  nextSort(field) {
    const sh: SortHelper = this.sortHelper;
    this.sortHelper = sh.nextSort(field);
  }
  changePage(event) {
    this.visible = false;
    this.currentPage = event;
    this.visible = true;
    this.historyDetails = null;
  }
  validTill(data) {
    const now = new Date();
    const date2 = new Date(data);
    const date2Utc = new Date(date2.getTime() + date2.getTimezoneOffset() * 60000);
    if(date2Utc.getTime() - now.getTime() > 0) {
      const leftUntil: any = date2Utc.getTime() - now.getTime();
      const days = Math.floor(leftUntil / 1000 / 60 / 60 / 24);
      const hours = Math.floor(leftUntil / 1000 / 60 / 60) % 24;
      const minutes = Math.floor(leftUntil / 1000 / 60) % 60;
      const seconds = Math.floor(leftUntil / 1000) % 60;
      return `Осталось <span class="text-success">${days > 0 ? days+'день ' : ''}  ${hours}:${minutes}:${seconds}</span>`;
    } else {
      return '<span class="text-danger">Ссылка не дествительная</span>'
    }
  }

  async getTransactionDetails(item) {
    this.renewSuccess = false
    this.historyDetails = item
  }
  renew(linkId: string) {
    this.paymentService.postPaymentLinkRenew(linkId).then(data => {
      if(data.data === 'SUCCESS') {
        this.renewSuccess = true;
        this.paymentService.getPaymentLinkList().then(rest => {
          this.cashBoxListHistory = rest.data.filter(a => a.cashBoxId === Number(this.cashBoxId)).reverse();
          this.historyDetails = rest.data.filter(a => a.guid === linkId)[0]
        })
      }
    })
  }
  async copy(text: string) {
    navigator.clipboard.writeText(text).then(() => {}).catch(err => {
      console.error(err.name, err.message);
    });
  }

}
