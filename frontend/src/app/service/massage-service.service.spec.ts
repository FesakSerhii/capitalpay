import { TestBed } from '@angular/core/testing';

import { MassageServiceService } from './massage-service.service';

describe('MassageServiceService', () => {
  let service: MassageServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MassageServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
