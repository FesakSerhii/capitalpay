package kz.capitalpay.server.paysystems.systems.halykbank.sevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HalykService {

    Logger logger = LoggerFactory.getLogger(HalykService.class);


    public String getPaymentButton() {

        return " <form class=\"tpsform_radioitem\" method=\"post\" action=\"https://api.capitalpay.kz/testsystem/pay\">" +

                "<button type=\"submit\">" +
                "<span class=\"tpsform_radioico\"><img src=\"/paysystems/img/NBK_Logo.png\" alt=\"\"></span>" +
                "<span class=\"tpsform_radioname\">АО \"Народный банк Казахстана\"</span>" +
                "</button>\n" +
                "</form>";

    }
}
