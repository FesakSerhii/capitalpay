import {Component, OnDestroy, OnInit} from "@angular/core";
import {CashBoxService} from "../../../../projects/admin-panel/src/app/service/cashbox.service";
import {UserService} from "../../../../projects/admin-panel/src/app/service/user.service";
import {SortHelper} from "../../helper/sort-helper";
import {PaymentService} from "../../service/payment.service";
import {FormControl} from "@angular/forms";
import {Subscription, timer} from "rxjs";

@Component({
    selector: "app-payment-links",
    templateUrl: "./payment-links.component.html",
    styleUrls: ["./payment-links.component.scss"]
})
export class PaymentLinksComponent implements OnInit, OnDestroy {
    cashBoxList: any = [];
    cashBoxListAll: any = [];
    merchantId: number = null;
    currentPage: number = 1;

    cashBoxId: FormControl = new FormControl(0);
    cashBoxListHistory: any = null;
    cashBoxListHistoryAll: any = [];
    subscription: Subscription = new Subscription();
    visible: boolean;
    sortHelper = new SortHelper();
    historyDetails: any = null;
    renewSuccess: boolean = false;

    constructor(private cashBoxService: CashBoxService, private userService: UserService, private paymentService: PaymentService) {
    }

    ngOnInit(): void {
        this.getCashBoxList();
        this.filterBoxList()
        this.cashBoxId.valueChanges.subscribe(a => {
            this.filterBoxList(a)
            this.historyDetails = null
        })
        this.subscription = timer(0, 60000).subscribe({
            next: (i) =>  {
                if(this.cashBoxListHistoryAll.length > 0) {
                    this.cashBoxListHistoryAll = this.cashBoxListHistoryAll.map(a => {
                        a.timeValid = this.validTill(a.validTill)
                        return a;
                    })
                }
            },
        })
    }

    filterBoxList(id = 0) {
        this.paymentService.getPaymentLinkList().then(rest => {
            this.cashBoxListHistory = rest.data.reverse();
            this.cashBoxListHistoryAll = this.cashBoxListHistory.map(a => {
                a.name = this.cashBoxList.filter(b => b.id === a.cashBoxId)[0].name;
                a.timeValid = this.validTill(a.validTill)
                return a
            })
            if(id !== 0) {
                this.cashBoxListHistoryAll = this.cashBoxListHistory.filter(h => h.cashBoxId === id)
            } else {
                this.cashBoxListHistoryAll = this.cashBoxListHistory
            }
            this.visible = true;
            this.currentPage = 1
        })
    }

    getCashBoxList() {
        this.merchantId = this.userService.getUserInfo().merchantId;
        this.cashBoxService.getCashBoxListByMerchantId(this.merchantId).then(resp => {
            this.cashBoxList = resp.data;
            this.cashBoxListAll = this.cashBoxList.map(a => {
                return {value: a.id, title: a?.name}
            });
            this.cashBoxListAll.unshift({title: 'Все', value: 0})
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

    async getTransactionDetails(item) {
        this.renewSuccess = false
        this.historyDetails = item
    }

    renew(linkId: string) {
        this.paymentService.postPaymentLinkRenew(linkId).then(data => {
            if (data.data === "SUCCESS") {
                this.renewSuccess = true;
                this.filterBoxList();
            }
        })
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
            return `Осталось: <strong><span class="text-success">${hours}:${minutes}</span></strong>`;
        } else {
            return '<span class="text-danger">Ссылка не дествительная</span>'
        }
    }

    async copy(text: string) {
        navigator.clipboard.writeText(text).then(() => {
        }).catch(err => {
            console.error(err.name, err.message);
        });
    }
    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }
}
