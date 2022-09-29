package kz.capitalpay.server.paysystems.systems.halyksoap.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class HalykController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HalykController.class);

    private final Gson gson;
    private final HalykSoapService halykSoapService;
    private final CashboxService cashboxService;

    @Value("${kkbsign.send.order.action.link}")
    String actionLink;

    public HalykController(Gson gson, HalykSoapService halykSoapService, CashboxService cashboxService) {
        this.gson = gson;
        this.halykSoapService = halykSoapService;
        this.cashboxService = cashboxService;
    }


    @PostMapping("/paysystem/halyk/listener")
    public void listener(@RequestParam String PaRes, @RequestParam String MD, @RequestParam(required = false) String TermUrl, HttpServletResponse response) {
        LOGGER.info("PaRes: {}", PaRes);
        LOGGER.info("MD: {}", MD);
        LOGGER.info("TermUrl: {}", TermUrl);

        String sessionid;
        Payment payment;
        // TODO: Костыль ради песочницы
        if (actionLink.equals("https://testpay.kkb.kz")) {
            sessionid = halykSoapService.getSessionByPaRes(PaRes);
            payment = halykSoapService.getPaymentByPaRes(PaRes);
        } else {
            sessionid = halykSoapService.getSessionByMD(MD);
            payment = halykSoapService.getPaymentByMd(MD);
        }

        LOGGER.info(sessionid);
        LOGGER.info(gson.toJson(payment));

        payment = halykSoapService.paymentOrderAcs(MD, PaRes, sessionid, false);
        String redirectUrl = cashboxService.getRedirectForPayment(payment);

        response.setHeader("Location", redirectUrl);
        response.setStatus(302);
    }
}
