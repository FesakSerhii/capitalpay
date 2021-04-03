import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { HexagonLinkComponent } from './hexagon-link/hexagon-link.component';
import {RouterModule} from "@angular/router";




@NgModule({
  declarations: [HeaderComponent, FooterComponent, HexagonLinkComponent],
    exports: [HeaderComponent, FooterComponent, HexagonLinkComponent],
    imports: [
        CommonModule,
        RouterModule
    ]
})
export class ComonBlocksModule { }
