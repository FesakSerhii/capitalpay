import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HexagonLinkComponent } from './hexagon-link.component';

describe('HexagonLinkComponent', () => {
  let component: HexagonLinkComponent;
  let fixture: ComponentFixture<HexagonLinkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HexagonLinkComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HexagonLinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
