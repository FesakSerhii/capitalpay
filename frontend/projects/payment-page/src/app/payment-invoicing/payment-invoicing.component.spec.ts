import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentInvoicingComponent } from './payment-invoicing.component';

describe('PaymentInvoicingComponent', () => {
  let component: PaymentInvoicingComponent;
  let fixture: ComponentFixture<PaymentInvoicingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PaymentInvoicingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentInvoicingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
