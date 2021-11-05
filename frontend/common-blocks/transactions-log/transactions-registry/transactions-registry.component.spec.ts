import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionsRegistryComponent } from './transactions-registry.component';

describe('TransactionsRegistryComponent', () => {
  let component: TransactionsRegistryComponent;
  let fixture: ComponentFixture<TransactionsRegistryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionsRegistryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionsRegistryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
