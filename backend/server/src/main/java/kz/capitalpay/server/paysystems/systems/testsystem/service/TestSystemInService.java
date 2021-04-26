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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.CHANGE_PAYMENT_STATUS;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.NOTIFY_CLIENT;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.INTERACTION_URL;
import static kz.capitalpay.server.simple.service.SimpleService.*;

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

    Random random = new Random();

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

//        return "<form method=\"post\" action=\"https://api.capitalpay.kz/testsystem/pay\">" +
//                "<input name=\"paymentid\" type=\"hidden\" value=\"" + payment.getGuid() + "\"/>" +
//                "<input name=\"billid\" type=\"hidden\" value=\"" + payment.getBillId() + "\"/>" +
//                "<input name=\"totalamount\" type=\"hidden\" value=\"" + payment.getTotalAmount().movePointRight(2) + "\"/>" +
//                "<input name=\"currency\" type=\"hidden\" value=\"" + payment.getCurrency() + "\"/>" +
//                "<p>" +
//                "<button type=\"submit\">Test Payment System</button>" +
//                "</p>" +
//                "</form>";
    }


    public void createPayment(String paymentid, String billid, BigDecimal totalamount, String currency) {
        try {
            TestsystemPayment payment = new TestsystemPayment();
            payment.setId(paymentid);
            payment.setTimestamp(System.currentTimeMillis());
            payment.setLocalDateTime(LocalDateTime.now());
            payment.setBillId(billid);
            payment.setTotalAmount(totalamount);
            payment.setCurrency(currency);
            payment.setStatus(NEW_PAYMENT);
            testsystemPaymentRepository.save(payment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TestsystemPayment setStatus(String paymentid, Long status) {
        try {
            TestsystemPayment payment = testsystemPaymentRepository.findById(paymentid).orElse(null);
            if (status == null || status < 1L || status > 3L) {
                status = 1L + random.nextInt(3);
            }

            if (status.equals(1L)) {
                payment.setStatus(SUCCESS);
            } else if (status.equals(2L)) {
                payment.setStatus(FAILED);
            } else if (status.equals(3L)) {
                payment.setStatus(PENDING);
            }
            testsystemPaymentRepository.save(payment);

            notifyCapitalPAyAboutStatusChange(payment);

            return payment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean notifyCapitalPAyAboutStatusChange(TestsystemPayment payment) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<TestsystemPayment> request =
                    new HttpEntity<>(payment, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity("https://api.capitalpay.kz/testsystem/interaction",
                            request, String.class);

            logger.info(response.getStatusCode().toString());
            if (response.hasBody()) {
                logger.info(response.getBody());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean notifyClientAboutStatusChange(Payment payment) {
        try {
         //   TODO: Signature

            String interactionUrl = cashboxSettingsService.getField(payment.getCashboxId(), INTERACTION_URL);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Payment> request =
                    new HttpEntity<>(payment, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(interactionUrl,
                            request, String.class);

            logger.info(response.getStatusCode().toString());
            if (response.hasBody()) {
                logger.info(response.getBody());
            }

            systemEventsLogsService.addNewPaysystemAction(testSystem.getComponentName(),
                    NOTIFY_CLIENT, gson.toJson(payment), payment.getMerchantId().toString());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getRedirectUrl() {
        return "https://api.capitalpay.kz/testsystem/redirecturl";
    }

    public String getRedirectUrlForPayment(String paymentid, String status) {

        Payment payment = paymentService.getPayment(paymentid);

        String redirectUrl =
                "https://api.capitalpay.kz/testshop/temporary/status?status=" + status + "&billid=" + payment.getBillId();

        return redirectUrl;
    }

    public String chаngePaymentStаtus(TestsystemPayment request) {

        logger.info(request.getId());
        Payment payment = paymentService.getPayment(request.getId());
        payment.setStatus(request.getStatus());
        systemEventsLogsService.addNewPaysystemAction(testSystem.getComponentName(), CHANGE_PAYMENT_STATUS,
                gson.toJson(request), payment.getMerchantId().toString());

        notifyClientAboutStatusChange(payment);

        return "OK";

    }
}
