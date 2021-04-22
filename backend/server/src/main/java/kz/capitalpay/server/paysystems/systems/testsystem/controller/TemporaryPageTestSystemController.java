package kz.capitalpay.server.paysystems.systems.testsystem.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.paysystems.systems.testsystem.model.TestsystemPayment;
import kz.capitalpay.server.paysystems.systems.testsystem.service.TestSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Controller
public class TemporaryPageTestSystemController {

    Logger logger = LoggerFactory.getLogger(TemporaryPageTestSystemController.class);

    @Autowired
    Gson gson;

    @Autowired
    TestSystemService testSystemService;

    @PostMapping("/testsystem/pay")
    String showTestPaymentPage(ModelMap map,
                               @RequestParam String paymentid,
                               @RequestParam String billid,
                               @RequestParam BigDecimal totalamount,
                               @RequestParam String currency,
                               HttpServletRequest httpRequest,
                               HttpServletResponse httpServletResponse) {

        map.addAttribute("paymentid", paymentid);
        map.addAttribute("billid", billid);
        map.addAttribute("totalamount", totalamount.movePointLeft(2));
        map.addAttribute("currency", currency);

        testSystemService.createPayment(paymentid, billid, totalamount, currency);

        return "testpaymenttmporary";
    }


    @PostMapping("/testsystem/buttonclick")
    void buttonClick(@RequestParam String paymentid,
                     @RequestParam Long status,
                     HttpServletResponse httpServletResponse) {

        TestsystemPayment payment = testSystemService.setStatus(paymentid, status);
        String redirectUrl = testSystemService.getRedirectUrl();

        httpServletResponse.setHeader("Location",
                redirectUrl + "?paimentid=" + payment.getId() + "&status=" + payment.getStatus());
        httpServletResponse.setStatus(302);
    }
}
