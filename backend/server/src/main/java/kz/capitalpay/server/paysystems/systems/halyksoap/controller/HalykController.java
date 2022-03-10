package kz.capitalpay.server.paysystems.systems.halyksoap.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.p2p.mapper.P2pPaymentMapper;
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

    @Autowired
    P2pPaymentMapper p2pPaymentMapper;

    @Value("${kkbsign.send.order.action.link}")
    String actionLink;


    @PostMapping("/paysystem/halyk/listener")
    public void listener(@RequestParam String PaReq,
                         @RequestParam String MD,
                         @RequestParam String TermUrl,
                         HttpServletResponse response) {
        logger.info("PaRes: {}", PaReq);
        logger.info("MD: {}", MD);
        logger.info("TermUrl: {}", TermUrl);


        String sessionid;
        Object payment;
        // TODO: Костыль ради песочницы
        if (actionLink.equals("https://testpay.kkb.kz")) {
            sessionid = halykSoapService.getSessionByPaRes(PaReq);
            payment = halykSoapService.getPaymentByPaRes(PaReq);
        } else {
            sessionid = halykSoapService.getSessionByMD(MD);
            payment = halykSoapService.getPaymentByMd(MD);
        }

        logger.info(sessionid);
        logger.info(gson.toJson(payment));

        payment = halykSoapService.paymentOrderAcs(MD, PaReq, sessionid, false);
        String redirectUrl = cashboxService.getRedirectForPayment((Payment) payment);

        response.setHeader("Location", redirectUrl);
        response.setStatus(302);
    }
}
