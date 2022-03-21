import { TestBed } from '@angular/core/testing';

import { BankErrorCodesService } from './bank-error-codes.service';

describe('BankErrorCodesService', () => {
  let service: BankErrorCodesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BankErrorCodesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
