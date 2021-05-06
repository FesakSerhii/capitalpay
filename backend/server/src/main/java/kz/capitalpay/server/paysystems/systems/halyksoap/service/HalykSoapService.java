package kz.capitalpay.server.paysystems.systems.halyksoap.service;

import com.google.gson.Gson;
import kz.capitalpay.server.paysystems.systems.halykbank.kkbsign.KKBSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
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

    String createPaymentOrderXML(BigDecimal amount, String cardholderName, String cvc, String desc,
                                 String month, String orderid, String pan, int trtype, String year) {

        String concatString = orderid + amount.toString().replace(".",",") + currency + trtype + pan + merchantid;
        logger.info("Concat String (orderid + amount + currency + trtype + pan + merchantid): {}",concatString);
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, alias, keypass, storepass);
        logger.info("Signature: {}",signatureValue);
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
                amount.toString().replace(".", ","),
                cardholderName, currency, cvc, desc, merchantid, month, orderid, pan, trtype, year,
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
            String signedXML = createPaymentOrderXML(amount, cardholderName, cvc, desc, month, orderid, pan, 1, year);
            Map<String, String> vars = new HashMap<>();
            vars.put("signedXML", signedXML);
            logger.info(signedXML);
            String response = restTemplate.postForObject(sendOrderActionLink + "/axis2/services/EpayService.EpayServiceHttpSoap12Endpoint/",
                    signedXML, String.class, java.util.Optional.ofNullable(null));
            logger.info("response: {}", response);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @PostConstruct
    public void testCreateXML() {
        try {
            Thread.sleep(5000);
            paymentPay(new BigDecimal("5.00"), "OLEG IVANOFF", "323", "Test payment SOAP",
                    "12", "0000000000234", "4003035000005378", "25");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
