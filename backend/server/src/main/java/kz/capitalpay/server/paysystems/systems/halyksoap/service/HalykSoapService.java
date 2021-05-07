package kz.capitalpay.server.paysystems.systems.halyksoap.service;

import com.google.gson.Gson;
import kz.capitalpay.server.paysystems.systems.halykbank.kkbsign.KKBSign;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykPaymentOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykPaymentOrderRepository;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class HalykSoapService {

    Logger logger = LoggerFactory.getLogger(HalykSoapService.class);

    @Autowired
    Gson gson;

    @Value("${halyk.soap.merchant.id}")
    String merchantid;

    @Value("${halyk.soap.currency}")
    String currency;

    @Value("${halyk.soap.merchantCertificate}")
    String merchantCertificate;

    @Value("${halyk.soap.keystore}")
    String keystore;

    @Value("${halyk.soap.alias}")
    String alias;

    @Value("${halyk.soap.keypass}")
    String keypass;

    @Value("${halyk.soap.storepass}")
    String storepass;

    @Value("${kkbsign.send.order.action.link}")
    String sendOrderActionLink;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    HalykPaymentOrderRepository halykPaymentOrderRepository;

    private String createPaymentOrderXML(HalykPaymentOrder paymentOrder, String cvc, String month, String year, String pan) {

        String concatString = paymentOrder.getOrderid() + paymentOrder.getAmount() + paymentOrder.getCurrency() +
                paymentOrder.getTrtype() + pan + paymentOrder.getMerchantid();
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, alias, keypass, storepass);
        String xml = String.format("<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
                        "<soapenv:Body>" +
                        "<ns4:paymentOrder xmlns:ns4=\"http://ws.epay.kkb.kz/xsd\">" +
                        "<order>" +
                        "<amount>%s</amount>" +
                        "<cardholderName>%s</cardholderName>" +
                        "<currency>%s</currency>" +
                        "<cvc>%s</cvc>" +
                        "<desc>%s</desc>" +
                        "<merchantid>%s</merchantid>" +
                        "<month>%s</month>" +
                        "<orderid>%s</orderid>" +
                        "<pan>%s</pan>" +
                        "<trtype>%d</trtype>" +
                        "<year>%s</year>" +
                        "</order>" +
                        "<requestSignature>" +
                        "<merchantCertificate>%s</merchantCertificate>" +
                        "<merchantId>%s</merchantId>" +
                        "<signatureValue>%s</signatureValue>" +
                        "</requestSignature>" +
                        "</ns4:paymentOrder>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>",
                paymentOrder.getAmount(), paymentOrder.getCardholderName(), paymentOrder.getCurrency(), cvc,
                paymentOrder.getDesc(), paymentOrder.getMerchantid(), month, paymentOrder.getOrderid(),
                pan, paymentOrder.getTrtype(), year,
                merchantCertificate, merchantid, signatureValue
        );
        return xml;

    }

    String createPaymentOrderAcsXML(String md, String pares, String sessionid) {
        String concatString = md + pares + sessionid;
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, alias, keypass, storepass);
        String xml = String.format(
                "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
                        "<soapenv:Body>" +
                        "<ns4:paymentOrderAcs xmlns:ns4=\"http://ws.epay.kkb.kz/xsd\">" +
                        "<order>" +
                        "<md>%s</md>" +
                        "<pares>%s</pares>" +
                        "<sessionid>%s</sessionid>" +
                        "</order>" +
                        "<requestSignature>" +
                        "<merchantCertificate>%s</merchantCertificate>" +
                        "<merchantId>%s</merchantId>" +
                        "<signatureValue>%s</signatureValue>" +
                        "</requestSignature>" +
                        "</ns4:paymentOrderAcs>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>",
                md, pares, sessionid, merchantCertificate, merchantid, signatureValue
        );
        return xml;
    }

    public boolean paymentPay(BigDecimal amount, String cardholderName, String cvc, String desc,
                              String month, String orderid, String pan, String year) {
        try {
            HalykPaymentOrder paymentOrder = new HalykPaymentOrder();
            paymentOrder.setTimestamp(System.currentTimeMillis());
            paymentOrder.setLocalDateTime(LocalDateTime.now());
            paymentOrder.setAmount(amount.setScale(2).toString());
            paymentOrder.setCardholderName(cardholderName);
            paymentOrder.setCurrency(currency);
            paymentOrder.setDesc(desc);
            paymentOrder.setMerchantid(merchantid);
            paymentOrder.setOrderid(orderid);
            paymentOrder.setTrtype(1);

            halykPaymentOrderRepository.save(paymentOrder);

            String signedXML = createPaymentOrderXML(paymentOrder, cvc, month, year, pan);
            Map<String, String> vars = new HashMap<>();
            vars.put("signedXML", signedXML);

            String response = restTemplate.postForObject(sendOrderActionLink + "/axis2/services/EpayService.EpayServiceHttpSoap12Endpoint/",
                    signedXML, String.class, java.util.Optional.ofNullable(null));
            logger.info("response: {}", response);

            parsePaymentOrderResponse(paymentOrder, response);
            logger.info(gson.toJson(paymentOrder));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private HalykPaymentOrder parsePaymentOrderResponse(HalykPaymentOrder paymentOrder, String response) {
        try {
            Document xmlDoc = DocumentHelper.createDocument();

            xmlDoc = DocumentHelper.parseText(response);
            logger.info("Full Document {}", xmlDoc.asXML());

            xmlDoc.getRootElement().addNamespace("ns", "http://ws.epay.kkb.kz/xsd");
            Element AscUrl = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/acsUrl");
            if (!AscUrl.getText().equals("null")) paymentOrder.setAcsUrl(AscUrl.getText());
            Element Approvalcode = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/approvalcode");
            if (!Approvalcode.getText().equals("null")) paymentOrder.setApprovalcode(Approvalcode.getText());
            Element Intreference = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/intreference");
            if (!Intreference.getText().equals("null")) paymentOrder.setIntreference(Intreference.getText());
            Element Is3ds = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/is3ds");
            if (!Is3ds.getText().equals("null")) paymentOrder.setIs3ds(Is3ds.getText().equals("true"));
            Element MD = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/md");
            if (!MD.getText().equals("null")) paymentOrder.setMd(MD.getText());
            Element Message = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/message");
            if (!Message.getText().equals("null")) paymentOrder.setMessage(Message.getText());
            Element Pareq = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/pareq");
            if (!Pareq.getText().equals("null")) paymentOrder.setPareq(Pareq.getText());
            Element Reference = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/reference");
            if (!Reference.getText().equals("null")) paymentOrder.setReference(Reference.getText());
            Element ReturnCode = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/returnCode");
            if (!ReturnCode.getText().equals("null")) paymentOrder.setReturnCode(ReturnCode.getText());
            Element Sessionid = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/sessionid");
            if (!Sessionid.getText().equals("null")) paymentOrder.setSessionid(Sessionid.getText());
            Element TermUrl = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/termUrl");
            if (!TermUrl.getText().equals("null")) paymentOrder.setTermUrl(TermUrl.getText());




        } catch (Exception e) {
            e.printStackTrace();
        }
        return paymentOrder;

    }


    @PostConstruct
    public void testCreateXML() {
        try {
            Thread.sleep(1000);
            paymentPay(new BigDecimal("5.00"), "OLEG IVANOFF", "323", "Test payment SOAP",
                    "12", "0000000074234", "4003035000005378", "25");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
