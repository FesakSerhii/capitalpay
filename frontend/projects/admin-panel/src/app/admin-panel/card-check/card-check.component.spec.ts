import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardCheckComponent } from './card-check.component';

describe('CardCheckComponent', () => {
  let component: CardCheckComponent;
  let fixture: ComponentFixture<CardCheckComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CardCheckComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CardCheckComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
