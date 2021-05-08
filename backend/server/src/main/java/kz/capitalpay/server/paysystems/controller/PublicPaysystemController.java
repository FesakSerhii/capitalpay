package kz.capitalpay.server.paysystems.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.service.PaysystemService;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/public/paysystem", produces = "application/json;charset=UTF-8")
public class PublicPaysystemController {

    Logger logger = LoggerFactory.getLogger(PublicPaysystemController.class);


    @Autowired
    Gson gson;

    @Autowired
    PaysystemService paysystemService;

    @Autowired
    HalykSoapService halykSoapService;

    @Autowired
    PaymentService paymentService;

//
//    @PostMapping("/system/buttonlist")
//    ResultDTO paysystemButtonList(@RequestBody PaymentRequestDTO request) {
//        logger.info("Paysystem Button List");
//        return paysystemService.systemButtonList(request);
//    }
//

    @PostMapping("/pay")
    void paymentCardPay(HttpServletRequest httpRequest,
                          HttpServletResponse httpResponse,
                          @RequestParam String paymentid,
                          @RequestParam String cardHolderName,
                          @RequestParam String cvc,
                          @RequestParam String month,
                          @RequestParam String pan,
                          @RequestParam String year
    ) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        logger.info("Payment ID: {}", paymentid);
        logger.info("Request IP: {}", ipAddress);
        logger.info("Request User-Agent: {}", httpRequest.getHeader("User-Agent"));

        Payment payment = paymentService.getPayment(paymentid);

        String result = halykSoapService.paymentOrder(payment.getTotalAmount(),
                cardHolderName, cvc, payment.getDescription(), month, payment.getPaySysPayId(), pan, year);
        if(result.equals("OK")){
            // TODO: сделать нормальную страницу успешного платежа
            httpResponse.setHeader("Location", "https://api.capitalpay.kz/testshop/page");
            httpResponse.setStatus(302);
        }else if (result.equals("FAIL")){
            // TODO: сделать нормальную страницу плохого платежа
            httpResponse.setHeader("Location", "https://capitalpay.kz/");
            httpResponse.setStatus(302);
        }else{
            httpResponse.setHeader("Location", result);
            httpResponse.setStatus(302);

        }
    }


}
