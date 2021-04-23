package kz.capitalpay.server.paysystems.systems.testsystem.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.CHANGE_PAYMENT_STATUS;
import static kz.capitalpay.server.simple.service.SimpleService.*;

@Service
public class TestSystemService {

    Logger logger = LoggerFactory.getLogger(TestSystemService.class);

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

    Random random = new Random();

    public String getPaymentButton(Payment payment) {
        return "<form method=\"post\" action=\"https://api.capitalpay.kz/testsystem/pay\">" +
                "<input name=\"paymentid\" type=\"hidden\" value=\"" + payment.getGuid() + "\"/>" +
                "<input name=\"billid\" type=\"hidden\" value=\"" + payment.getBillId() + "\"/>" +
                "<input name=\"totalamount\" type=\"hidden\" value=\"" + payment.getTotalAmount().movePointRight(2) + "\"/>" +
                "<input name=\"currency\" type=\"hidden\" value=\"" + payment.getCurrency() + "\"/>" +
                "<p>" +
                "<button type=\"submit\">Test Payment System</button>" +
                "</p>" +
                "</form>";
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

            notifyClientAboutStatusChange(payment);

            return payment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean notifyClientAboutStatusChange(TestsystemPayment payment) {
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

        return "OK";

    }
}
