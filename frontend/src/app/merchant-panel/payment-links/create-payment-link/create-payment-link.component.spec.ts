import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatePaymentLinkComponent } from './create-payment-link.component';

describe('CreatePaymentLinkComponent', () => {
  let component: CreatePaymentLinkComponent;
  let fixture: ComponentFixture<CreatePaymentLinkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreatePaymentLinkComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreatePaymentLinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
