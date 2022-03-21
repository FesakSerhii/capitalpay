import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {
  width:number = 0;

  constructor() { }

  ngOnInit(): void {
    this.width = document.body.clientWidth;
  }

}
