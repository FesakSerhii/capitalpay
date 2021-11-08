import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentsLayoutsComponent } from './documents-layouts.component';

describe('DocumentsLayoutsComponent', () => {
  let component: DocumentsLayoutsComponent;
  let fixture: ComponentFixture<DocumentsLayoutsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentsLayoutsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentsLayoutsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
