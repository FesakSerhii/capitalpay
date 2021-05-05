package kz.capitalpay.server.paysystems.systems.halykbank.sevice;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxService;
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
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static kz.capitalpay.server.simple.service.SimpleService.*;

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

    @Value("${remote.api.addres}")
    String remoteApiAddress;

    @Value("${kkbsign.merchant_id}")
    String halykMerchantId;

    @Value("${kkbsign.certificate}")
    String halykCertId;

    @Autowired
    CashboxSettingsService cashboxSettingsService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    HalykPaymentRepository halykPaymentRepository;

    @Autowired
    CashboxService cashboxService;

    public String getPaymentButton(Payment payment) {

        HalykPayment halykPayment = new HalykPayment();
        halykPayment = halykPaymentRepository.saveAndFlush(halykPayment);
        halykPayment.setTimestamp(System.currentTimeMillis());
        halykPayment.setLocalDateTime(LocalDateTime.now());
        halykPayment.setBillId(payment.getBillId());
        halykPayment.setCashboxId(payment.getCashboxId());
        halykPayment.setCurrency(payment.getCurrency());
        halykPayment.setPaymentId(payment.getGuid());
        halykPayment.setStatus(payment.getStatus());
        halykPayment.setTotalAmount(payment.getTotalAmount());


        logger.info("Halyk Payment: ", gson.toJson(halykPayment));
        Long id = halykPayment.getId();
        String paddingID = String.format("%1$14s", id)
                .replace(' ', '0');
        logger.info(paddingID);
        halykPayment.setHalykId(paddingID);

        KKBSign kkbSign = new KKBSign();

        String base64Content = kkbSign.build64(kkbsignCfgPath, payment.getTotalAmount().toString(), paddingID);

        String ticket = "<document><item number=\"1\" name=\"Оплата\" quantity=\"1\" " +
                "amount=\"" + payment.getTotalAmount().toString() + "\"/></document>";

        String appendix = new String(Base64.encode((ticket).getBytes()));

//        String backLink = cashboxSettingsService.getField(payment.getCashboxId(), CashboxSettingsService.REDIRECT_SUCCESS_URL);
//        String backLink = cashboxSettingsService.getField(payment.getCashboxId(), CashboxSettingsService.REDIRECT_SUCCESS_URL);
        String backLink = remoteApiAddress + "/halyk/backlink?paymentid=" + paddingID;
//        String failureBackLink = cashboxSettingsService.getField(payment.getCashboxId(), CashboxSettingsService.REDIRECT_FAILED_URL);

        String postLink = cashboxSettingsService.getField(payment.getCashboxId(), CashboxSettingsService.INTERACTION_URL);

        String form = "<form class=\"tpsform_radioitem\" name=\"SendOrder\" method=\"post\" action=\"" + sendOrderActionLink + "/jsp/process/logon.jsp\">" +
                "<input type=\"hidden\" name=\"Signed_Order_B64\" value=\"" + base64Content + "\"><br>" +
                "<input type=\"hidden\" name=\"email\" size=50 maxlength=50  value=\"" + mailUser + "\"><br>" +
                "<input type=\"hidden\" name=\"Language\" value=\"rus\"><br>" +
                "<input type=\"hidden\" name=\"BackLink\" value=\"" + backLink + "\"><br>" +
                "<input type=\"hidden\" name=\"PostLink\" value=\"" + postLink + "\"><br>" +
//                "<input type=\"hidden\" name=\"FailureBackLink\" value=\"" + failureBackLink + "\"><br>" +
                "<input type=\"hidden\" name=\"appendix\" size=50 maxlength=50 value=\"" + appendix + "\"/><br>" +
                "<button type=\"submit\" name=\"GotoPay\">" +
                "<span class=\"tpsform_radioico\"><img src=\"/paysystems/img/NBK_Logo.png\" alt=\"\"></span>" +
                "<span class=\"tpsform_radioname\">АО \"Народный банк Казахстана\"</span>" +
                "</button>\n" +
                "</form>";
        halykPayment.setForm(form);
        halykPaymentRepository.save(halykPayment);

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

            HalykPayment halykPayment = halykPaymentRepository.findTopByHalykId(order_id);
            halykPayment.setXmlResponse(response);
            halykPaymentRepository.save(halykPayment);

            Payment paymentFromBd = paymentService.getPayment(halykPayment.getPaymentId());

            if (!paymentFromBd.getTotalAmount().equals(new BigDecimal(amount))) {
                logger.error("Amount not equal. Payment summ: {} Order amount {}", paymentFromBd.getTotalAmount(), amount);
                return -1;
            }

            KKBSign kkbsign = new KKBSign();

            String ks = kkbsignCfgBank;
            boolean result = kkbsign.verify(sBank.trim(), sbank_sign.trim(), ks, "kkbca", "1q2w3e4r");
            logger.info("Verify signature :{}", result);
            if (!result) {
                return -1;
            }
            boolean complited = paymentComplite(xmlDoc);
            if (complited) {
                logger.info("...Process Payment....");

                halykPayment.setStatus(SUCCESS);
                halykPaymentRepository.save(halykPayment);
                paymentService.success(paymentFromBd);
                return 0;
            } else {
                logger.error("Payment complited: {}", complited);
            }
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
            logger.info("Merchant: {}", Merchant.asXML());
            logger.info("cert_id: {}", cert_id);

            String merchantXML = String.format("<merchant id=\"%s\"><command type=\"complete\"/><payment reference=\"%s\" approval_code=\"%s\" orderid=\"%s\" amount=\"%s\" currency_code=\"%s\"/></merchant>",
                    merchant_id, reference, approval_code, orderid, amount, currency_code);
            logger.info("merchantXML: {}", merchantXML);
            KKBSign kkbSign = new KKBSign();
            Map<String, String> config = kkbSign.getConfig(kkbsignCfgPath);
            String signature = kkbSign.sign64(merchantXML,
                    config.get("keystore"),
                    config.get("alias"),
                    config.get("keypass"),
                    config.get("storepass"));
            String signedXML = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><document>%s<merchant_sign type=\"RSA\" cert_id=\"%s\">%s</merchant_sign></document>",
                    merchantXML, cert_id, signature);
            logger.info("signedXML: {}", signedXML);

            Map<String, String> vars = new HashMap<>();
            vars.put("signedXML", URLEncoder.encode(signedXML,Charset.defaultCharset()));

            String response = restTemplate.getForObject(sendOrderActionLink + "/jsp/remote/control.jsp?{signedXML}",
                    String.class, vars);
            logger.info("response: {}", response);
            // TODO: analyze response

            // TODO: EventLog


            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getRedirectUrlForPayment(String halykId) {
        try {
            HalykPayment halykPayment = halykPaymentRepository.findTopByHalykId(halykId);
            String status = getPaymentStatus(halykPayment);
            String billid = halykPayment.getBillId();
            String paymentid = halykPayment.getPaymentId();
            String url = cashboxSettingsService.getField(halykPayment.getCashboxId(), CashboxSettingsService.REDIRECT_URL)
                    + "?status=" + status
                    + "&billid=" + billid
                    + "&paymentid=" + paymentid;

            return url;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Halyk ID: {}", halykId);
        }
        return null;
    }

    private String getPaymentStatus(HalykPayment halykPayment) {
        try {

            String merchant_id = halykMerchantId;
            String cert_id = halykCertId;
            String orderid = halykPayment.getHalykId();

            String merchantXML = String.format("<merchant id=\"%s\"><order id=\"%s\"/></merchant>",
                    merchant_id, orderid);
            logger.info("merchantXML: {}", merchantXML);
            KKBSign kkbSign = new KKBSign();
            Map<String, String> config = kkbSign.getConfig(kkbsignCfgPath);
            String signature = kkbSign.sign64(merchantXML,
                    config.get("keystore"),
                    config.get("alias"),
                    config.get("keypass"),
                    config.get("storepass"));
            String signedXML = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><document>%s<merchant_sign type=\"RSA\" cert_id=\"%s\">%s</merchant_sign></document>",
                    merchantXML, cert_id, URLEncoder.encode(signature, "UTF-8"));
            logger.info("signedXML: {}", signedXML);

            Map<String, String> vars = new HashMap<>();
            vars.put("signedXML", signedXML);

            String response = restTemplate.getForObject(sendOrderActionLink + "/jsp/remote/checkOrdern.jsp?{signedXML}",
                    String.class, vars);
            logger.info("response: {}", response);


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
            Element Merchant = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank/merchant");
            logger.info("Merchant: {}", Merchant.asXML());
            Element Order = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank/merchant/order");
            Element sResponse = (Element) xmlDoc.getRootElement().selectSingleNode("//document/bank/response");
            logger.info("Order: {}", Order.asXML());
            String order_id = Order.attribute("id").getValue();
            logger.info("order_id : {}", order_id);

            String payment = sResponse.attribute("payment").getValue();
            logger.info("payment={}", payment);
            String status = sResponse.attribute("status").getValue();
            logger.info("status={}", status);
            String sResult = sResponse.attribute("result").getValue();
            logger.info("result={}", sResult);


            KKBSign kkbsign = new KKBSign();

            String ks = kkbsignCfgBank;
            boolean result = kkbsign.verify(sBank.trim(), sbank_sign.trim(), ks, "kkbca", "1q2w3e4r");
            logger.info("Verify signature :{}", result);
            if (!result) {
                return null;
            }

            if (payment.equals("true")) {
                if (status.equals("0")) {
                    return PENDING;
                } else if (status.equals("2")) {
                    return SUCCESS;
                }
            }

            return FAILED;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: временно пока не разобрались с кодирвоанием подписи
        return SUCCESS;

    }
}
