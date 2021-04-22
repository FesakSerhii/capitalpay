package kz.capitalpay.server.paysystems.systems.testsystem.service;

import kz.capitalpay.server.payments.model.Payment;
import org.springframework.stereotype.Service;

@Service
public class TestSystemService {


    public String getPaymentButton(Payment payment) {
        return "<form method=\"post\" action=\"https://api.capitalpay.kz/pay\">\n" +
                "                <input name=\"cashboxid\" type=\"hidden\" value=\"1\"/>\n" +
                "                <input name=\"billid\" type=\"hidden\" value=\"ORD-0123-2021-01-25-A\"/>\n" +
                "                <input name=\"totalamount\" type=\"hidden\" value=\"12000\"/>\n" +
                "                <input name=\"currency\" type=\"hidden\" value=\"KZT\"/>\n" +
                "                <p>\n" +
                "                    <button type=\"submit\">Test Payment System</button>\n" +
                "                </p>\n" +
                "            </form>";
    }
}
