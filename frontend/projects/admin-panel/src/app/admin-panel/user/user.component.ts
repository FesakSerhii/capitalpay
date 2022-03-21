import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {UserService} from '../../service/user.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Router} from '@angular/router';
import {FormArray, FormControl, FormGroup, Validators} from '@angular/forms';
import {SortHelper} from '../../../../../../src/app/helper/sort-helper';
import {SearchInputService} from '../../../../../../src/app/service/search-input.service';
import {log} from 'util';
import {Timestamp} from 'rxjs';
import {dateCompareValidator} from '../../../../../../common-blocks/validators/dateCompareValidator';
import {MassageModalComponent} from '../../../../../../common-blocks/massage-modal/massage-modal.component';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {
  @ViewChild('addNewUser', {static: false}) addNewUserModal: TemplateRef<any>;
  @ViewChild('invalidDatesModalContent', {static: false}) invalidDatesModal: MassageModalComponent;

  constructor(private userService: UserService, private modalService: NgbModal, private router: Router, private searchInputService: SearchInputService,) {
  }

  userList: any[] = [];
  dontTouched: any[] = [];
  roleList = {
    ROLE_USER: 'Пользователь',
    ROLE_OPERATOR: 'Оператор',
    ROLE_ADMIN: 'Админ',
    ROLE_MERCHANT: 'Мерчант'
  };
  statuses = {
    ACTIVE: 'активен',
    BLOCKED: 'заблокирован',
    INACTIVE: 'не активирован'
  }
  newUserForm = new FormGroup({
    password: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.email]),
    username: new FormControl(''),
    roleList: new FormArray([new FormControl(Validators.required)])
  });
  filter = new FormGroup({
    roles: new FormControl(),
    dateStart: new FormControl(),
    dateEnd: new FormControl(),
    username: new FormControl(),
    status: new FormControl(),
    realname: new FormControl(),
    id: new FormControl(),
    email: new FormControl(),
  })
  isFilterActive = {
    roles: false,
    dateStart: false,
    dateEnd: false,
    username: false,
    realname: false,
    id: false,
    email: false,
    status: false,
  }
  confirmPassword = new FormControl();
  possibleRoles: [any] = null;
  possibleStatuses = [
    {title: 'активен', value: 'ACTIVE'},
    {title: 'заблокирован', value: 'BLOCKED'},
    {title: 'не активирован', value: 'INACTIVE'}];
  possibleRolesList: [any] = null;
  sortHelper = new SortHelper();
  tableSearch = new FormControl();

  ngOnInit(): void {
    let dateFields = {
      dateStart: 'dateEnd',
    };
    for (let v in dateFields) {
      this.filter.get(v).setValidators([Validators.required, dateCompareValidator(this.filter, dateFields[v])]);
      this.filter.get(dateFields[v]).setValidators([Validators.required, dateCompareValidator(this.filter, v, true)]);
    }
    this.getUsers();
    this.tableSearch.valueChanges.subscribe(val => {
      if (val.length > 3) {
        this.userList = this.searchInputService.filterData(this.sortedActions, val)
      } else {
        this.userList = [...this.dontTouched];
      }
    })
    this.getPossibleRoles();
  }

  getPossibleRoles() {
    this.userService.getUserRolesList().then(resp => {
      this.possibleRolesList = resp.data;
      this.possibleRoles = resp.data.map(el => {
        return {title: this.roleList[el.authority], value: el.id}
      });
    })
  }

  async getUsers(isSearchAfter=false) {
    this.userService.getUserList().then(resp => {
      this.dontTouched = resp.data;
      // this.dontTouched.map((el) => {
      //   let role = '';
      //   for (const userRole of el.roles) {
      //     role = role === '' ? role + this.roleList[userRole.authority] : role + '/' + this.roleList[userRole.authority]
      //   }
      //   el.roles = role;
      // })
      this.userList = [...this.dontTouched]
      if(isSearchAfter){
        this.search()
      }
    })
  }
  converseUserList(role){
      let roleStr = '';
      for (const userRole of role) {
        roleStr =  roleStr === '' ? roleStr + this.roleList[userRole.authority] : roleStr + '/' + this.roleList[userRole.authority]
      }
      return roleStr
  }

  addForm(form) {
    form.push(new FormControl(''));
  }

  deleteLastForm(form) {
    form.removeAt(form.controls.length - 1)
  }

  open() {
    this.modalService.open(this.addNewUserModal, {windowClass: ''});
  }

  close() {
    this.modalService.dismissAll()
  }

  navigateToSettings(id) {
    this.router.navigate(['/admin-panel/user/settings'], {
      queryParams: {
        userId: id,
      },
      queryParamsHandling: 'merge'
    })
  }

  createUser() {
    for (let role in this.newUserForm.value['roleList']) {
      this.newUserForm.value['roleList'][role] = this.possibleRolesList.filter(el => el.id === this.newUserForm.value['roleList'][role])[0].authority;
    }
    this.newUserForm.value.phone = '+7' + this.newUserForm.value.phone;
    this.userService.createUser(this.newUserForm.value).then(resp => {
      this.getUsers();
    })
  }

  comparePasswords() {
    return this.newUserForm.value.password === this.confirmPassword.value
  }

  nextSort(field) {
    let sh: SortHelper = this.sortHelper;
    this.sortHelper = sh.nextSort(field);
  }

  get sortedActions() {
    if (this.sortHelper.sort.sortBy === null) {
      this.userList = this.searchInputService.filterData(this.dontTouched, this.tableSearch.value);
      return this.userList
    } else {
      let sorted = this.dontTouched.sort(
        (a, b) => {
          let aField = a[this.sortHelper.sort.sortBy];
          let bField = b[this.sortHelper.sort.sortBy];
          let res = aField == bField ? 0 : (aField > bField ? 1 : -1);
          return this.sortHelper.sort.increase ? res : -res;
        }
      );
      sorted = this.searchInputService.filterData(sorted, '');
      this.userList = [...sorted];
      return this.userList
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
    if (isNewSearch) {
      this.getUsers(true)
    }else{
      this.search()
    }

  }
  search(){
    for (const control in this.filter.value) {
      let value = this.filter.value[control];
      if(value){
        this.isFilterActive[control] = this.filter.value[control] !== null;
        if (this.isFilterActive[control]) {
          if (this.filter.value[control] && control !== 'dateStart' && control !== 'dateEnd') {
            this.dontTouched = this.dontTouched.filter(el => {
                if (control === 'status') {
                  return el[control] === value
                } else if(control === 'roles'){
                  return el[control].filter(el=>el.id===value).length!==0
                }else {
                  return el[control] !== null ? `${el[control]}`.toLowerCase().includes(`${value}`.trim().toLowerCase()) : false
                }
              }
            )
          } else {
            this.dontTouched = this.dontTouched.filter(el => {
              return this.filter.value.dateStart && this.filter.value.dateEnd ? this.compareDates(this.filter.value.dateStart, this.filter.value.dateEnd, el[control]) : false
            })
          }
        }
        this.nextSort(null)
      }
    }
  }

  compareDates(dateValueStart, dateValueEnd, dateData) {
    const startDate = dateValueStart ? new Date(`${dateValueStart.year}-${dateValueStart.month}-${dateValueStart.day}`).getTime() : null;
    const endDate = dateValueEnd ? new Date(`${dateValueEnd.year}-${dateValueEnd.month}-${dateValueEnd.day}`).getTime() : null;
    if (startDate === null) {
      return dateData <= endDate
    } else if (endDate === null) {
      return dateData >= startDate
    } else {
      return dateData >= startDate && dateData <= endDate
    }
  }

  clearFilter() {
    this.filter.reset()
    this.getUsers();
  }

  dateFromTimestamp(timestamp) {
    return new Date(timestamp)
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
