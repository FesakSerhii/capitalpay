import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardBindingComponent } from './card-binding.component';

describe('CardBindingComponent', () => {
  let component: CardBindingComponent;
  let fixture: ComponentFixture<CardBindingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CardBindingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CardBindingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
