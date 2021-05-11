package kz.capitalpay.server.paysystems.systems.halyksoap.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static kz.capitalpay.server.simple.service.SimpleService.FAILED;
import static kz.capitalpay.server.simple.service.SimpleService.SUCCESS;

@Controller
public class HalykController {

    Logger logger = LoggerFactory.getLogger(HalykController.class);

    @Autowired
    Gson gson;

    @Autowired
    HalykSoapService halykSoapService;

    @Autowired
    CashboxService cashboxService;


    @PostMapping("/paysystem/halyk/listener")
    @ResponseBody
    public String listener(@RequestParam String PaRes,
                           @RequestParam String MD,
                           @RequestParam String TermUrl) {
        logger.info("PaRes: {}", PaRes);
        logger.info("MD: {}", MD);
        logger.info("TermUrl: {}", TermUrl);

        String sessionid = halykSoapService.getSessionByMD(MD);

        boolean result = halykSoapService.paymentOrderAcs(MD, PaRes, sessionid);
        String url = "";
        Payment payment = halykSoapService.getPaymentByMd(MD);

        if (result) {
            url = cashboxService.getUrlByPayment(payment, SUCCESS);
        } else {
            url = cashboxService.getUrlByPayment(payment, FAILED);
        }
        return PaRes;
    }


}
