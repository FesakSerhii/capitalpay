package kz.capitalpay.server.paysystems.systems.testsystem.controller;

import kz.capitalpay.server.paysystems.systems.testsystem.model.TestsystemPayment;
import kz.capitalpay.server.paysystems.systems.testsystem.service.TestSystemFantomService;
import kz.capitalpay.server.paysystems.systems.testsystem.service.TestSystemInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Controller
public class PageTestSystemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageTestSystemController.class);

    private final TestSystemInService testSystemInService;
    private final TestSystemFantomService testSystemFantomService;

    public PageTestSystemController(TestSystemInService testSystemInService, TestSystemFantomService testSystemFantomService) {
        this.testSystemInService = testSystemInService;
        this.testSystemFantomService = testSystemFantomService;
    }

    @PostMapping("/testsystem/pay")
    String showTestPaymentPage(ModelMap map,
                               @RequestParam String paymentid,
                               @RequestParam String billid,
                               @RequestParam BigDecimal totalamount,
                               @RequestParam String currency) {
        map.addAttribute("paymentid", paymentid);
        map.addAttribute("billid", billid);
        map.addAttribute("totalamount", totalamount.movePointLeft(2));
        map.addAttribute("currency", currency);
        testSystemFantomService.createPayment(paymentid, billid, totalamount, currency);
        return "testsystem/cardpay";
    }


    @PostMapping("/testsystem/buttonclick")
    void buttonClick(@RequestParam String paymentid,
                     @RequestParam Long status,
                     HttpServletResponse httpServletResponse) {
        TestsystemPayment payment = testSystemFantomService.setStatus(paymentid, status);
        String redirectUrl = testSystemInService.getRedirectUrl();
        httpServletResponse.setHeader("Location",
                redirectUrl + "?paymentid=" + payment.getId() + "&status=" + payment.getStatus());
        httpServletResponse.setStatus(302);
    }


    @GetMapping("/testsystem/redirecturl")
    void getRedirectUrl(@RequestParam String paymentid,
                        @RequestParam String status,
                        HttpServletResponse httpServletResponse) {
        LOGGER.info(paymentid);
        LOGGER.info(status);
        String redirectUrl = testSystemInService.getRedirectUrlForPayment(paymentid, status);
        httpServletResponse.setHeader("Location", redirectUrl);
        httpServletResponse.setStatus(302);
    }
}
