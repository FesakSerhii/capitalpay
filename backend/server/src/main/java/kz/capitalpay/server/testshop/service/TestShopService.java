package kz.capitalpay.server.testshop.service;

import org.springframework.stereotype.Service;

@Service
public class TestShopService {

    Long lastBillId = System.currentTimeMillis();

    public Long getNewBillId(){
        return lastBillId+=100;
    }

}
