import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-help',
  templateUrl: './help.component.html',
  styleUrls: ['./help.component.scss']
})
export class HelpComponent implements OnInit {
  activeTab = 'tab1';
  constructor( private router: Router) { }

  ngOnInit(): void {
  }
  navigateToSettings(){
    this.router.navigate(['/admin-panel/help/chat'])
  }
}
