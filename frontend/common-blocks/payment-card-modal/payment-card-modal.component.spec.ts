import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentCardModalComponent } from './payment-card-modal.component';

describe('PaymentCardModalComponent', () => {
  let component: PaymentCardModalComponent;
  let fixture: ComponentFixture<PaymentCardModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PaymentCardModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentCardModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
