package kz.capitalpay.server.paysystems.systems.halyksoap.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.constants.HalykOrderDictionary;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class HalykController {

    Logger logger = LoggerFactory.getLogger(HalykController.class);

    @Autowired
    Gson gson;

    @Autowired
    HalykSoapService halykSoapService;

    @Autowired
    CashboxService cashboxService;

    @Value("${kkbsign.send.order.action.link}")
    String actionLink;


    @PostMapping("/paysystem/halyk/listener")
    public void listener(@RequestParam String PaRes,
                         @RequestParam String MD,
                         @RequestParam(required = false) String TermUrl,
                         HttpServletResponse response) {
        logger.info("PaRes: {}", PaRes);
        logger.info("MD: {}", MD);
        logger.info("TermUrl: {}", TermUrl);


        String sessionid;
        Object payment;
        // TODO: Костыль ради песочницы
        if (actionLink.equals("https://testpay.kkb.kz")) {
            sessionid = halykSoapService.getSessionByPaRes(PaRes);
            payment = halykSoapService.getPaymentByPaRes(PaRes);
        } else {
            sessionid = halykSoapService.getSessionByMD(MD);
            payment = halykSoapService.getPaymentByMd(MD);
        }

        logger.info(sessionid);
        logger.info(gson.toJson(payment));

        payment = halykSoapService.paymentOrderAcs(MD, PaRes, sessionid, HalykOrderDictionary.PAYMENT_ORDER);
        String redirectUrl = cashboxService.getRedirectForPayment((Payment) payment);

        response.setHeader("Location", redirectUrl);
        response.setStatus(302);
    }
}
