import {Component, OnInit, ViewChild} from "@angular/core";
import {UserService} from "../../projects/admin-panel/src/app/service/user.service";
import {Router} from "@angular/router";
import {PaymentsService} from "../../projects/admin-panel/src/app/service/payments.service";
import {SortHelper} from "../../src/app/helper/sort-helper";
import {SearchInputService} from "../../src/app/service/search-input.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {dateCompareValidator} from "../validators/dateCompareValidator";
import {MassageModalComponent} from "../massage-modal/massage-modal.component";
import {Observable, Subscription} from "rxjs";
import {debounceTime, distinctUntilChanged, map, switchMap} from "rxjs/operators";

@Component({
    selector: "app-transactions-log",
    templateUrl: "./transactions-log.component.html",
    styleUrls: ["./transactions-log.component.scss"]
})
export class TransactionsLogComponent implements OnInit {

    @ViewChild("invalidDatesModalContent", {static: false}) invalidDatesModal: MassageModalComponent;

    constructor(private userService: UserService, private router: Router, private paymentsService: PaymentsService, private searchInputService: SearchInputService) {
    }

    activeTab = "tab1";
    user: any;
    isAdmin: boolean;
    transactionContent: any = null;
    transactionList: any[] = null;
    merchantNames: any[] = null;
    dontTouched: any[] = null;
    transactionDetails: any = null;
    currentPage: number = 1;
    totalElements: number = 1;
    visible: boolean = true;
    selectedTransaction: any = null;
    sortHelper = new SortHelper();
    showItem: FormControl = new FormControl(10);
    filter = new FormGroup({
        billId: new FormControl(),
        dateBefore: new FormControl(),
        dateStart: new FormControl(),
        dateAfter: new FormControl(),
        dateEnd: new FormControl(),
        merchantName: new FormControl(),
        paymentId: new FormControl(),
        totalAmount: new FormControl(),
        currency: new FormControl(),
        cashboxName: new FormControl(),
    })
    isFilterActive = {
        billId: false,
        dateStart: false,
        dateEnd: false,
        merchantName: false,
        paymentId: false,
        totalAmount: false,
        currency: false,
        cashboxName: false,
    }
    currencyList = [];
    tableSearch = new FormControl();

    ngOnInit(): void {
        let dateFields = {
            dateStart: "dateEnd",
        };
        for (let v in dateFields) {
            this.filter.get(v).setValidators([Validators.required, dateCompareValidator(this.filter, dateFields[v])]);
            this.filter.get(dateFields[v]).setValidators([Validators.required, dateCompareValidator(this.filter, v, true)]);
        }
        this.user = this.userService.getUserInfo();
        this.isAdmin = this.router.url.includes("admin-panel");
        this.getTransactions(1);
        this.tableSearch.valueChanges.subscribe(val => {
            if (val.length > 3) {
                this.transactionList = this.searchInputService.filterData(this.sortedActions, val)
            } else {
                this.transactionList = [...this.dontTouched];
            }
        })
        this.showItem.valueChanges.subscribe(a => {
            this.getTransactions(1);
        })

    }

    searchMerchantNames: (text$: Observable<string>) => Observable<string[]> = (text$: Observable<string>) => text$.pipe(
        debounceTime(200),
        distinctUntilChanged(),
        switchMap((name: any) => {
            if(name.length > 1) {
                return this.paymentsService.postMerchantNames({searchText: name}).then(a => {
                    return a.data.map(i => i.name);
                });
            } else {
                return [];
            }
        }),
    );

    async getTransactions(page: number = 1, filterForm: any = this.filter.value, sort: any = {}) {
        this.transactionContent = {
            ...await this.paymentsService.getTransactionsList({
                ...{
                    page,
                    limit: this.showItem.value,
                    sortDto: sort
                }, ...filterForm
            })
        }.data;
        this.transactionList = this.transactionContent.content;
        this.totalElements = this.transactionContent.totalElements;

        this.dontTouched = [...this.transactionList]
        for (const item of this.transactionList) {
            if (this.currencyList.indexOf(item.currency) === -1) {
                this.currencyList.push(item.currency)
            }
        }
    }

    formRegistre() {
        this.router.navigate([(this.isAdmin ? "admin-panel" : "/merchant") + "/transaction-log/registry"])
    }

    changePage(event) {
        this.getTransactions(event)
        this.visible = false;
        this.currentPage = event;
        this.visible = true;
        this.transactionDetails = null;

    }

    async getTransactionDetails(id) {
        const obj = {...await this.paymentsService.getTransactionDetails(id)}.data
        const arr = [];
        for (const field in obj) {
            arr.push({field: field, value: obj[field]})
        }
        this.transactionDetails = [...arr];

        this.transactionDetails = this.transactionDetails.map((item: any) => {
            switch (item.field) {
                case "outgoing":
                    item.label = "Направление платежа"
                    item.value = item.value ? "Входной" : "Выходной"
                    break;
                case "payerPan":
                    item.label = "Карта отправителя"
                    break;
                case "payerPhone":
                    item.label = "Телефон отправителя"
                    break;
                case "payerName":
                    item.label = "Имя отправителя"
                    break;
                case "payerEmail":
                    item.label = "Email отправителя"
                    break;
                case "receiverPan":
                    item.label = "Карта получателя"
                    break;
                case "receiverPhone":
                    item.label = "Телефон получателя"
                    break;
                case "receiverName":
                    item.label = "Имя получателя"
                    break;
                case "receiverEmail":
                    item.label = "Email получателя "
                    break;
            }
            return item;
        })
    }

    getGuid(data) {
        return data.filter(el => el.field === "guid")[0].value
    }

    async nextSort(field) {
        let sh: SortHelper = this.sortHelper;
        this.sortHelper = sh.nextSort(field);
        await this.getTransactions(this.currentPage, this.filter.value, {
            field: this.sortHelper.sortBy,
            asc: this.sortHelper.increase
        })
    }

    get sortedActions() {
        if (this.sortHelper.sort.sortBy === null) {
            this.transactionList = this.searchInputService.filterData(this.dontTouched, this.tableSearch.value);
            return this.transactionList
        } else {
            return this.transactionList
        }
    }

    async filterFields() {
        let isNewSearch = false
        for (const flag in this.isFilterActive) {
            if (this.isFilterActive[flag]) {
                isNewSearch = true
                this.isFilterActive[flag] = false;
            }
        }

        for (const control in this.filter.value) {
            let value: any = this.filter.value[control];
            this.isFilterActive[control] = true;
            if (this.filter.value[control] && control !== "dateStart" && control !== "dateEnd" && control !== "dateBefore" && control !== "dateAfter") {
                this.dontTouched = this.dontTouched.filter(el => {
                    return el[control] !== null ? `${el[control]}`.toLowerCase().includes(value.trim().toLowerCase()) : false
                })
            } else if (control === "dateStart" && this.filter.value[control]) {
                this.filter.value.dateAfter = this.compareDates(this.filter.value.dateStart)
            } else if (control === "dateEnd" && this.filter.value[control]) {
                this.filter.value.dateBefore = this.compareDates(this.filter.value.dateEnd)
            }
        }

        await this.getTransactions(1, this.filter.value);
        this.nextSort(null)
    }

    compareDates(dateValueStart) {
        return dateValueStart ? `${dateValueStart.year}-${String(dateValueStart.month).padStart(2, "0")}-${String(dateValueStart.day).padStart(2, "0")}` : null;
    }

    clearFilter() {
        this.filter.reset()
        this.changePage(1)
    }

    dateInvalid(start, end) {
        if (this.filter.get(start).value !== null && this.filter.get(end).value !== null) {
            setTimeout(() => {
                if (this.filter.get(start).invalid || this.filter.get(end).invalid) {
                    this.invalidDatesModal.open()
                    this.filter.get(start).reset();
                    this.filter.get(end).reset();
                }
            }, 200);
        }
    }
}
