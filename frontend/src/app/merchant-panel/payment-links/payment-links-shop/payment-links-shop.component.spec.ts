import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentLinksShopComponent } from './payment-links-shop.component';

describe('PaymentLinksShopComponent', () => {
  let component: PaymentLinksShopComponent;
  let fixture: ComponentFixture<PaymentLinksShopComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PaymentLinksShopComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentLinksShopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
