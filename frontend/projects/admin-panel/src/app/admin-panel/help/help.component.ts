import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {SupportService} from "../../service/support.service";

@Component({
  selector: 'app-help',
  templateUrl: './help.component.html',
  styleUrls: ['./help.component.scss']
})
export class HelpComponent implements OnInit {
  activeTab = 'tab1';
  supportList: any;
  constructor( private router: Router, private supportService: SupportService) { }

  ngOnInit(): void {
    this.getSupportList()
  }
  getSupportList(){
    this.supportService.getSupportList().then(resp=>{
      this.supportList = resp.data;
    })
  }
  navigateToSettings(id){
    this.router.navigate(['/admin-panel/help/chat'],{queryParams: {
      id: id,
    },
      queryParamsHandling: "merge"})
  }
}
