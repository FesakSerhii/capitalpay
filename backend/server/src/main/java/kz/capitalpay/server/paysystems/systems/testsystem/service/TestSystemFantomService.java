package kz.capitalpay.server.paysystems.systems.testsystem.service;

import kz.capitalpay.server.paysystems.systems.testsystem.model.TestsystemPayment;
import kz.capitalpay.server.paysystems.systems.testsystem.repository.TestsystemPaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import static kz.capitalpay.server.simple.service.SimpleService.*;

@Service
public class TestSystemFantomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSystemFantomService.class);

    private final TestsystemPaymentRepository testsystemPaymentRepository;
    private final TestSystemInService testSystemInService;

    Random random = new Random();

    public TestSystemFantomService(TestsystemPaymentRepository testsystemPaymentRepository, TestSystemInService testSystemInService) {
        this.testsystemPaymentRepository = testsystemPaymentRepository;
        this.testSystemInService = testSystemInService;
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


    private void notifyCapitalPAyAboutStatusChange(TestsystemPayment payment) {
        try {
            testSystemInService.changePaymentStatus(payment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String checkPendingStatus(String paymentid) {
        TestsystemPayment payment = setStatus(paymentid, 4L);
        return payment.getStatus();
    }
}
