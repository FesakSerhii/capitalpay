import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentLayoutsEditorComponent } from './document-layouts-editor.component';

describe('DocumentLayoutsEditorComponent', () => {
  let component: DocumentLayoutsEditorComponent;
  let fixture: ComponentFixture<DocumentLayoutsEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentLayoutsEditorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentLayoutsEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
