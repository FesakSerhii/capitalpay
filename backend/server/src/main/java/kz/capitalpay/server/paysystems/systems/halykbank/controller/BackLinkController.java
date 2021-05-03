package kz.capitalpay.server.paysystems.systems.halykbank.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.paysystems.systems.halykbank.sevice.HalykService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class BackLinkController {

    Logger logger = LoggerFactory.getLogger(BackLinkController.class);

    @Autowired
    Gson gson;

    @Autowired
    HalykService halykService;

    @GetMapping("/halyk/backlink")
    public void backLink(@RequestParam String paymentid, HttpServletRequest request, HttpServletResponse response) {

        String redirectUrl = halykService.getRedirectUrlForPayment(paymentid);
        if (redirectUrl == null) {
            logger.error("Payment ID: {}", paymentid);
            try {
                response.getWriter().write("Error");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            response.setHeader("Location", redirectUrl);
            response.setStatus(302);
        }
    }

}
