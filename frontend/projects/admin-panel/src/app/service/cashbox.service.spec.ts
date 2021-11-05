import { TestBed } from '@angular/core/testing';

import { CashboxService } from './cashbox.service';

describe('CashboxService', () => {
  let service: CashboxService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CashboxService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
