package kz.capitalpay.server.testshop.service;

import kz.capitalpay.server.paysystems.systems.testsystem.service.TestSystemFantomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestShopService {

    Long lastBillId = System.currentTimeMillis();

    @Autowired
    TestSystemFantomService testSystemFantomService;

    public Long getNewBillId() {
        return lastBillId += 100;
    }


    public String checkPendingStatus(String paymentid) {
        return testSystemFantomService.checkPendingStatus(paymentid);
    }
}
