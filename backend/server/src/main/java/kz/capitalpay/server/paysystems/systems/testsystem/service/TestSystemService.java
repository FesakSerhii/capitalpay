package kz.capitalpay.server.paysystems.systems.testsystem.service;

import com.google.gson.Gson;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestSystemService {

    Logger logger = LoggerFactory.getLogger(TestSystemService.class);

    @Autowired
    Gson gson;

    @Autowired
    PaymentService paymentService;

    public String getPaymentButton(Payment payment) {
        return "<form method=\"post\" action=\"https://api.capitalpay.kz/testsystem/pay\">" +
                "<input name=\"billid\" type=\"hidden\" value=\""+payment.getBillId()+"\"/>" +
                "<input name=\"totalamount\" type=\"hidden\" value=\""+payment.getTotalAmount().movePointRight(2)+"\"/>" +
                "<input name=\"currency\" type=\"hidden\" value=\""+payment.getCurrency()+"\"/>" +
                "<p>" +
                "<button type=\"submit\">Test Payment System</button>" +
                "</p>" +
                "</form>";
    }
}
