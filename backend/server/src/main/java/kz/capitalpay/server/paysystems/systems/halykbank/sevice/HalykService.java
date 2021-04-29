package kz.capitalpay.server.paysystems.systems.halykbank.sevice;

import com.google.gson.Gson;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.systems.halykbank.kkbsign.Base64;
import kz.capitalpay.server.paysystems.systems.halykbank.kkbsign.KKBSign;
import kz.capitalpay.server.paysystems.systems.halykbank.model.HalykPayment;
import kz.capitalpay.server.paysystems.systems.halykbank.repository.HalykPaymentRepository;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class HalykService {

    Logger logger = LoggerFactory.getLogger(HalykService.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Gson gson;


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

    @Autowired
    PaymentService paymentService;

    @Autowired
    HalykPaymentRepository halykPaymentRepository;

    public String getPaymentButton(Payment payment) {

        HalykPayment halykPayment = new HalykPayment();
        halykPayment.setTimestamp(System.currentTimeMillis());
        halykPayment.setLocalDateTime(LocalDateTime.now());
        halykPayment.setBillId(payment.getBillId());
        halykPayment.setCashboxId(payment.getCashboxId());
        halykPayment.setCurrency(payment.getCurrency());
        halykPayment.setPaymentId(payment.getGuid());
        halykPayment.setStatus(payment.getStatus());
        halykPayment.setTotalAmount(payment.getTotalAmount());

       halykPayment = halykPaymentRepository.save(halykPayment);

        logger.info("Halyk Payment: ", gson.toJson(halykPayment));
        Long id = halykPayment.getId();
        halykPayment.setHalykId(String.format("%1$14s", id.toString())
                .replace(' ', '0'));
        logger.info("Halyk ID: ", halykPayment.getHalykId());

        KKBSign kkbSign = new KKBSign();

        String base64Content = kkbSign.build64(kkbsignCfgPath, payment.getTotalAmount().toString(), payment.getBillId());

        String ticket = "<document><item number=\"1\" name=\"Оплата\" quantity=\"1\" " +
                "amount=\"" + payment.getTotalAmount().toString() + "\"/></document>";

        String appendix = new String(Base64.encode((ticket).getBytes()));

        String backLink = cashboxSettingsService.getField(payment.getCashboxId(), CashboxSettingsService.REDIRECT_SUCCESS_URL);
        String failureBackLink = cashboxSettingsService.getField(payment.getCashboxId(), CashboxSettingsService.REDIRECT_FAILED_URL);
        String postLink = cashboxSettingsService.getField(payment.getCashboxId(), CashboxSettingsService.INTERACTION_URL);

        String form = "<form class=\"tpsform_radioitem\" name=\"SendOrder\" method=\"post\" action=\"" + sendOrderActionLink + "/jsp/process/logon.jsp\">" +
                "<input type=\"hidden\" name=\"Signed_Order_B64\" value=\"" + base64Content + "\"><br>" +
                "<input type=\"hidden\" name=\"email\" size=50 maxlength=50  value=\"" + mailUser + "\"><br>" +
                "<input type=\"hidden\" name=\"Language\" value=\"rus\"><br>" +
                "<input type=\"hidden\" name=\"BackLink\" value=\"" + backLink + "\"><br>" +
                "<input type=\"hidden\" name=\"PostLink\" value=\"" + postLink + "\"><br>" +
                "<input type=\"hidden\" name=\"FailureBackLink\" value=\"" + failureBackLink + "\"><br>" +
                "<input type=\"hidden\" name=\"appendix\" size=50 maxlength=50 value=\"" + appendix + "\"/><br>" +
                "<button type=\"submit\" name=\"GotoPay\">" +
                "<span class=\"tpsform_radioico\"><img src=\"/paysystems/img/NBK_Logo.png\" alt=\"\"></span>" +
                "<span class=\"tpsform_radioname\">АО \"Народный банк Казахстана\"</span>" +
                "</button>\n" +
                "</form>";
        halykPayment.setForm(form);

        return form;
    }


    public int setPaySystemResponse(String response, String appendix) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String PostLink = response;
            logger.info("PostLink: {}", PostLink);

            Document xmlDoc = DocumentHelper.createDocument();
            xmlDoc = DocumentHelper.parseText(PostLink);
            logger.info("Full Document {}", xmlDoc.asXML());


            Element Bank = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank");
            String sBank = Bank.asXML() + "";
            logger.info("Element bank: sBank={}", sBank);
            Element bank_sign = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank_sign");
            String sbank_sign = bank_sign.getText();
            logger.info("Element bank_sign: sbank_sign={}", sbank_sign);
            Element Merchant = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank/customer/merchant");
            logger.info("Merchant: {}", Merchant.asXML());
            Element Order = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank/customer/merchant/order");
            Element results = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank/results");
            Element payment = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank/results/payment");
            logger.info("Order: {}", Order.asXML());
            String order_id = Order.attribute("order_id").getValue();
            logger.info("order_id : {}", order_id);
            String amount = Order.attribute("amount").getValue();
            String bik = Bank.attribute("bik").getValue();
            String name = Bank.attribute("name").getValue();
            String cardNumber = payment.attribute("card").getValue();
            String reference = payment.attribute("reference").getValue();
            String approvalCode = payment.attribute("approval_code").getValue();
            LocalDateTime paymentTime = LocalDateTime.parse(results.attribute("timestamp").getValue(), formatter);
            logger.info("paymentTime {}", paymentTime);
            logger.info("amount: {}", amount);

            Payment paymentFromBd = paymentService.getPaymentByOrderId(order_id);

            if (paymentFromBd.getTotalAmount().equals(new BigDecimal(amount))) {
                if (paymentComplite(xmlDoc)) {
                    logger.info("....TODO: Process Payment....");

                }
            } else {
                logger.error("Amount not equal. Payment summ: {} Order amount {}", paymentFromBd.getTotalAmount(), amount);
            }

            KKBSign kkbsign = new KKBSign();

            String ks = kkbsignCfgBank;
            String result = kkbsign.verify(sBank.trim(), sbank_sign.trim(), ks, "kkbca", "1q2w3e4r") + "";
            logger.info("Verify signature :{}", result);

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean paymentComplite(Document xmlDoc) {
        try {
            Element Payment = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank/results/payment");
            Element Merchant = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank/customer/merchant");
            Element Order = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank/customer/merchant/order");
            String merchant_id = Payment.attribute("merchant_id").getValue();
            String reference = Payment.attribute("reference").getValue();
            String approval_code = Payment.attribute("approval_code").getValue();
            String orderid = Order.attribute("order_id").getValue();
            String amount = Payment.attribute("amount").getValue();
            String currency_code = Order.attribute("currency").getValue();
            String cert_id = Merchant.attribute("cert_id").getValue();

            String merchantXML = String.format("<merchant id=\"%s\"><command type=\"complete\"/><payment reference=\"%s\" approval_code=\"%s\" orderid=\"%s\" amount=\"%s\" currency_code=\"%s\"/></merchant>",
                    merchant_id, reference, approval_code, orderid, amount, currency_code);
            logger.info(merchantXML);
            KKBSign kkbSign = new KKBSign();
            Map<String, String> config = kkbSign.getConfig(kkbsignCfgPath);
            String signature = kkbSign.sign64(merchantXML,
                    config.get("keystore"),
                    config.get("alias"),
                    config.get("keypass"),
                    config.get("storepass"));
            String signedXML = String.format("<document>%s<merchant_sign type=\"RSA\" cert_id=\"%s\">%s</merchant_sign></document>",
                    merchantXML, cert_id, signature);
            logger.info(signedXML);
            String response = restTemplate.getForObject(sendOrderActionLink + "/jsp/remote/control.jsp", String.class);
            logger.info(response);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
