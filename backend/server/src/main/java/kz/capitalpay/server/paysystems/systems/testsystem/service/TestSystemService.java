package kz.capitalpay.server.paysystems.systems.testsystem.service;

import com.google.gson.Gson;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.systems.testsystem.model.TestsystemPayment;
import kz.capitalpay.server.paysystems.systems.testsystem.repository.TestsystemPaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static kz.capitalpay.server.simple.service.SimpleService.NEW_PAYMENT;

@Service
public class TestSystemService {

    Logger logger = LoggerFactory.getLogger(TestSystemService.class);

    @Autowired
    Gson gson;

    @Autowired
    PaymentService paymentService;

    @Autowired
    TestsystemPaymentRepository testsystemPaymentRepository;

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
        try{
            TestsystemPayment payment = new TestsystemPayment();
            payment.setId(paymentid);
            payment.setTimestamp(System.currentTimeMillis());
            payment.setLocalDateTime(LocalDateTime.now());
            payment.setBillId(billid);
            payment.setTotalAmount(totalamount);
            payment.setCurrency(currency);
            payment.setStatus(NEW_PAYMENT);
            testsystemPaymentRepository.save(payment);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
