import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-hexagon-link',
  templateUrl: './hexagon-link.component.html',
  styleUrls: ['./hexagon-link.component.scss']
})
export class HexagonLinkComponent implements OnInit {

  @Input()src = '';
  constructor() { }

  ngOnInit(): void {
  }

}
