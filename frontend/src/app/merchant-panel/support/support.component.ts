import {Component, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {FileService} from '../../service/file.service';
import {SupportService} from '../../service/support.service';
import {SortHelper} from '../../helper/sort-helper';
import {SearchInputService} from '../../service/search-input.service';
import {MassageModalComponent} from '../../../../common-blocks/massage-modal/massage-modal.component';

@Component({
  selector: 'app-support',
  templateUrl: './support.component.html',
  styleUrls: ['./support.component.scss']
})
export class SupportComponent implements OnInit {

  @ViewChild('massageModal', {static: false}) massageModal: MassageModalComponent;

  constructor(private fileService: FileService, private supportService: SupportService, private searchInputService: SearchInputService) {
  }

  activeTab: string = 'tab1';
  sortHelper = new SortHelper();
  tableSearch = new FormControl();
  supportList: any = [];
  chosenFile: any = [];
  dontTouched: any[] = [];
  questionForm = new FormGroup({
    'theme': new FormControl(),
    'subject': new FormControl(''),
    'text': new FormControl()
  });
  fileToUpload: File = null;
  fileList: any[] = [];

  ngOnInit(): void {
    this.supportService.getSupportListByMerchantId().then(resp => {
      this.supportList = resp.data;
      this.dontTouched = [...resp.data];
    })
  }

  nextSort(field) {
    let sh: SortHelper = this.sortHelper;
    this.sortHelper = sh.nextSort(field);
  }

  get sortedActions() {
    if (this.sortHelper.sort.sortBy === null) {
      this.supportList = this.searchInputService.filterData(this.dontTouched, this.tableSearch.value);
      return this.supportList
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
      this.supportList = [...sorted];
      return this.supportList
    }
  }

  send() {
    if (this.chosenFile) {
      let promises = []
      for (let file of this.chosenFile) {
        promises.push(this.fileService.sendFile(file))
      }
      Promise.all(promises).then(resp => {
        resp.map(el => {
          console.log(el);
          this.fileList.push(el.data.id)
        })
      })
    }
    let request = {
      'fileList': this.fileList
    }
    Object.assign(request, this.questionForm.value)
    this.supportService.sendSupportRequest(request).then(resp => {
      this.massageModal.open();
      this.chosenFile = [];
      this.questionForm.reset();
    })
  }

  sendFiles() {

  }

  fileChosen(files: FileList) {
    if (files.length > 0) {
      this.chosenFile.push(files[0]);
    }
    console.log(this.chosenFile);
  }

  deleteFile(index) {
    this.chosenFile.splice(index, 1)
  }
}
