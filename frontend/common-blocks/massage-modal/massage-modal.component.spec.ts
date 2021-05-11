import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MassageModalComponent } from './massage-modal.component';

describe('MassageModalComponent', () => {
  let component: MassageModalComponent;
  let fixture: ComponentFixture<MassageModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MassageModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MassageModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
