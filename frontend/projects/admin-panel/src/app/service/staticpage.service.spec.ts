import { TestBed } from '@angular/core/testing';

import { StaticPageService } from './static-page.service';

describe('StaticpageService', () => {
  let service: StaticPageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StaticPageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
