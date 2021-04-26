package kz.capitalpay.server.paysystems.systems.testsystem.service;

import com.google.gson.Gson;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import static kz.capitalpay.server.simple.service.SimpleService.*;
import static kz.capitalpay.server.simple.service.SimpleService.PENDING;

@Service
public class TestSystemFantomService {

    Logger logger = LoggerFactory.getLogger(TestSystemFantomService.class);

    @Autowired
    Gson gson;

    @Autowired
    TestsystemPaymentRepository testsystemPaymentRepository;

    @Autowired
    TestSystemOutService testSystemOutService;

    @Autowired
    TestSystemInService testSystemInService;

    Random random = new Random();

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
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<TestsystemPayment> request =
//                    new HttpEntity<>(payment, headers);
//
//            ResponseEntity<String> response =
//                    restTemplate.postForEntity("https://api.capitalpay.kz/testsystem/interaction",
//                            request, String.class);
//
//            logger.info(response.getStatusCode().toString());
//            if (response.hasBody()) {
//                logger.info(response.getBody());
//            }

            testSystemInService.chаngePaymentStаtus(payment);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
