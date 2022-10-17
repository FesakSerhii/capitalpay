import { Component, OnInit } from '@angular/core';
import {CashBoxService} from "../../../../projects/admin-panel/src/app/service/cashbox.service";
import {UserService} from "../../../../projects/admin-panel/src/app/service/user.service";

@Component({
  selector: 'app-payment-links',
  templateUrl: './payment-links.component.html',
  styleUrls: ['./payment-links.component.scss']
})
export class PaymentLinksComponent implements OnInit {
  cashBoxList: any = null;
  merchantId: number = null;
  currentPage:number = 1;
  constructor(private cashBoxService: CashBoxService, private userService: UserService) { }

  ngOnInit(): void {
    this.getCashBoxList();
  }

  getCashBoxList() {
    this.merchantId = this.userService.getUserInfo().merchantId;
    this.cashBoxService.getCashBoxListByMerchantId(this.merchantId).then(resp => {
      this.cashBoxList = resp.data;
    })
  }

}
