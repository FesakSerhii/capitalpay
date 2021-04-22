package kz.capitalpay.server.simple.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class RedirectUrlController {

    Logger logger = LoggerFactory.getLogger(RedirectUrlController.class);

    @GetMapping("/payment/simple/redirecturl")
    void getRedirectUrl(@RequestParam String paymentid,
                        @RequestParam String status,
                        HttpServletResponse httpServletResponse) {


        logger.info(paymentid);
        logger.info(status);

//        TestsystemPayment payment = testSystemService.setStatus(paymentid, status);
//        String redirectUrl = testSystemService.getRedirectUrl();
//
//        httpServletResponse.setHeader("Location",
//                redirectUrl + "?paimentid=" + payment.getId() + "&status=" + payment.getStatus());
//        httpServletResponse.setStatus(302);

    }
}

