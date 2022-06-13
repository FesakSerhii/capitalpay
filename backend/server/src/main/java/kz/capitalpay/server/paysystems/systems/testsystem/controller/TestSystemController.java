package kz.capitalpay.server.paysystems.systems.testsystem.controller;

import kz.capitalpay.server.paysystems.systems.testsystem.service.TestSystemInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/testsystem", produces = "application/json;charset=UTF-8")
public class TestSystemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSystemController.class);

    @Autowired
    TestSystemInService testSystemInService;

//    @PostMapping("/interaction")
//    String chаngePaymentStаtus(@Valid @RequestBody TestsystemPayment request) {
//        logger.info("Notify from Test Payment System");
//        return testSystemInService.chаngePaymentStаtus( request);
//    }
}
