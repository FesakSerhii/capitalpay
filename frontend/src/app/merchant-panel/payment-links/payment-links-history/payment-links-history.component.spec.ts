import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentLinksHistoryComponent } from './payment-links-history.component';

describe('PaymentLinksHistoryComponent', () => {
  let component: PaymentLinksHistoryComponent;
  let fixture: ComponentFixture<PaymentLinksHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PaymentLinksHistoryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentLinksHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
