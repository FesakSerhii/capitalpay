package kz.capitalpay.server.testshop.controller;


import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.testshop.service.TestShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/testshop", produces = "application/json;charset=UTF-8")
public class TestShopBillController {

    @Autowired
    TestShopService testShopService;

    @PostMapping("/newbillid")
    ResultDTO getNewBillId() {
        Long billId = testShopService.getNewBillId();
        return new ResultDTO(true, billId, 0);
    }
}
