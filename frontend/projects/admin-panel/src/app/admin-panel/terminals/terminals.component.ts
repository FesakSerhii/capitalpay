import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TerminalService} from "../../service/terminal.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {NgxSpinnerService} from "ngx-spinner";

@Component({
  selector: 'app-terminals',
  templateUrl: './terminals.component.html',
  styleUrls: ['./terminals.component.scss']
})
export class TerminalsComponent implements OnInit {
  terminals$ = this.terminalService.getTerminals();
  terminalForm = new FormGroup({
    name: new FormControl("", [Validators.required]),
    inputTerminalId: new FormControl("", [Validators.required]),
    outputTerminalId: new FormControl("", [Validators.required])
  });
  @ViewChild("terminalEditorModal") termModal: TemplateRef<any>;
  currentTerminalId: number;
  constructor(private terminalService: TerminalService, private modalService: NgbModal, private spinner: NgxSpinnerService) { }

  ngOnInit(): void {
  }

  editTerminal(t: any) {
    this.currentTerminalId = t.id;
    this.terminalForm.patchValue(t);
    this.modalService.open(this.termModal);
  }

  deleteTerminal(t: any) {
    if (confirm(`Вы правда хотите удалить терминал ${t.name}`)) {
      this.terminalService.deleteTerminal(t.id).subscribe(() => {
        this.terminals$ = this.terminalService.getTerminals();
      })
    }
  }

  addTerminal() {
    this.terminalForm.reset();
    this.currentTerminalId = undefined;
    this.modalService.open(this.termModal);
  }

  storeTerminal() {
    if (!this.terminalForm.valid) {
      return this.terminalForm.markAllAsTouched();
    }
    const data = {...this.terminalForm.value};
    if (this.currentTerminalId) {
      data.id = this.currentTerminalId;
    }
    const obs$ = this.currentTerminalId ? this.terminalService.updateTerminal(data): this.terminalService.createTerminal(data);
    obs$.subscribe(() => {
      this.terminals$ = this.terminalService.getTerminals();
      this.modalService.dismissAll();
    })
  }
}
