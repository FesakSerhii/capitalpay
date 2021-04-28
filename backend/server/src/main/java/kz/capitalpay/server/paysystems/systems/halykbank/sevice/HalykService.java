package kz.capitalpay.server.paysystems.systems.halykbank.sevice;

import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.merchantsettings.model.CashboxSettings;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halykbank.kkbsign.Base64;
import kz.capitalpay.server.paysystems.systems.halykbank.kkbsign.KKBSign;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class HalykService {

    Logger logger = LoggerFactory.getLogger(HalykService.class);

    @Autowired
    RestTemplate restTemplate;


    @Value("${kkbsign.cfg.path}")
    String kkbsignCfgPath;

    @Value("${kkbsign.cfg.bank}")
    String kkbsignCfgBank;

    @Value("${kkbsign.send.order.action.link}")
    String sendOrderActionLink;

    @Value("${kkbsign.post.link}")
    String postLink;

    @Value("${mail.user}")
    String mailUser;

    @Autowired
    CashboxSettingsService cashboxSettingsService;

    public String getPaymentButton(Payment payment) {

        KKBSign kkbSign = new KKBSign();

        String base64Content = kkbSign.build64(kkbsignCfgPath, payment.getTotalAmount().toString(), payment.getBillId());

        String ticket = "<document><item number=\"1\" name=\"Оплата\" quantity=\"1\" " +
                "amount=\"" + payment.getTotalAmount().toString() + "\"/></document>";

        String appendix = new String(Base64.encode((ticket).getBytes()));

        String backLink = cashboxSettingsService.getField(payment.getCashboxId(),CashboxSettingsService.REDIRECT_SUCCESS_URL);
        String failureBackLink = cashboxSettingsService.getField(payment.getCashboxId(),CashboxSettingsService.REDIRECT_FAILED_URL);
        String postLink = cashboxSettingsService.getField(payment.getCashboxId(),CashboxSettingsService.INTERACTION_URL);

        return "<form class=\"tpsform_radioitem\" name=\"SendOrder\" method=\"post\" action=\"" + sendOrderActionLink + "\">" +
                "<input type=\"hidden\" name=\"Signed_Order_B64\" value=\"" + base64Content + "\"><br>" +
                "<input type=\"hidden\" name=\"email\" size=50 maxlength=50  value=\"" + mailUser + "\"><br>" +
                "<input type=\"hidden\" name=\"Language\" value=\"rus\"><br>" +
                "<input type=\"hidden\" name=\"BackLink\" value=\""+backLink+"\"><br>" +
                "<input type=\"hidden\" name=\"PostLink\" value=\""+postLink+"\"><br>" +
                "<input type=\"hidden\" name=\"FailureBackLink\" value=\""+failureBackLink+"\"><br>" +
                "<input type=\"hidden\" name=\"appendix\" size=50 maxlength=50 value=\"" + appendix + "\"/><br>" +
                "<button type=\"submit\" name=\"GotoPay\">" +
                "<span class=\"tpsform_radioico\"><img src=\"/paysystems/img/NBK_Logo.png\" alt=\"\"></span>" +
                "<span class=\"tpsform_radioname\">АО \"Народный банк Казахстана\"</span>" +
                "</button>\n" +
                "</form>";

    }



    public int setPaySystemResponse(String response, String appendix) {
        try {

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


}
