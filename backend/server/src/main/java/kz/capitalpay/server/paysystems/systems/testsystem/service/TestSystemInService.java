package kz.capitalpay.server.paysystems.systems.testsystem.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.systems.testsystem.TestSystem;
import kz.capitalpay.server.paysystems.systems.testsystem.model.TestsystemPayment;
import kz.capitalpay.server.paysystems.systems.testsystem.repository.TestsystemPaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.CHANGE_PAYMENT_STATUS;

@Service
public class TestSystemInService {

    Logger logger = LoggerFactory.getLogger(TestSystemInService.class);

    @Autowired
    Gson gson;

    @Autowired
    PaymentService paymentService;

    @Autowired
    TestsystemPaymentRepository testsystemPaymentRepository;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;

    @Autowired
    TestSystem testSystem;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CashboxService cashboxService;

    @Autowired
    CashboxSettingsService cashboxSettingsService;

    @Autowired
    ApplicationUserService applicationUserService;



    public String getPaymentButton(Payment payment) {

        return " <form class=\"tpsform_radioitem\" method=\"post\" action=\"https://api.capitalpay.kz/testsystem/pay\">" +
                "<input name=\"paymentid\" type=\"hidden\" value=\"" + payment.getGuid() + "\"/>" +
                "<input name=\"billid\" type=\"hidden\" value=\"" + payment.getBillId() + "\"/>" +
                "<input name=\"totalamount\" type=\"hidden\" value=\"" + payment.getTotalAmount().movePointRight(2) + "\"/>" +
                "<input name=\"currency\" type=\"hidden\" value=\"" + payment.getCurrency() + "\"/>" +
                "<button type=\"submit\">" +
                "<span class=\"tpsform_radioico\"><img src=\"/paysystems/img/form1_ico2.png\" alt=\"\"></span>" +
                "<span class=\"tpsform_radioname\">Test payment system</span>" +
                "</button>\n" +
                "</form>";

    }


    public String getRedirectUrl() {
        return "https://api.capitalpay.kz/testsystem/redirecturl";
    }

    public String getRedirectUrlForPayment(String paymentid, String status) {

        Payment payment = paymentService.getPayment(paymentid);

        String redirectUrl =
                "https://api.capitalpay.kz/testshop/status?status=" + status
                        + "&billid=" + payment.getBillId()
                        + "&paymentid=" + paymentid;

        return redirectUrl;
    }

    public String chаngePaymentStаtus(TestsystemPayment request) {

        logger.info(request.getId());
        Payment payment = paymentService.getPayment(request.getId());
        payment.setStatus(request.getStatus());
        systemEventsLogsService.addNewPaysystemAction(testSystem.getComponentName(), CHANGE_PAYMENT_STATUS,
                gson.toJson(request), payment.getMerchantId().toString());

//        testSystemOutService.notifyClientAboutStatusChange(payment);

        return "OK";

    }
}
