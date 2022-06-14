package kz.capitalpay.server.paysystems.systems.testsystem.service;

import com.google.gson.Gson;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.systems.testsystem.model.TestsystemPayment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.CHANGE_PAYMENT_STATUS;

@Service
public class TestSystemInService {

    private static final Logger logger = LoggerFactory.getLogger(TestSystemInService.class);

    private final Gson gson;
    private final PaymentService paymentService;
    private final SystemEventsLogsService systemEventsLogsService;
    private static final String COMPONENT_NAME = "TestSystem";

    public TestSystemInService(Gson gson, PaymentService paymentService, SystemEventsLogsService systemEventsLogsService) {
        this.gson = gson;
        this.paymentService = paymentService;
        this.systemEventsLogsService = systemEventsLogsService;
    }


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
        return "https://api.capitalpay.kz/testshop/status?status=" + status
                + "&billid=" + payment.getBillId()
                + "&paymentid=" + paymentid;
    }

    public void changePaymentStatus(TestsystemPayment request) {
        logger.info(request.getId());
        Payment payment = paymentService.getPayment(request.getId());
        payment.setStatus(request.getStatus());
        systemEventsLogsService.addNewPaysystemAction(COMPONENT_NAME, CHANGE_PAYMENT_STATUS,
                gson.toJson(request), payment.getMerchantId().toString());

//        testSystemOutService.notifyClientAboutStatusChange(payment);
    }
}
