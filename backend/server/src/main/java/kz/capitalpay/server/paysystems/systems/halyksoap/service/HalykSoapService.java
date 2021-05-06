package kz.capitalpay.server.paysystems.systems.halyksoap.service;

import com.google.gson.Gson;
import kz.capitalpay.server.paysystems.systems.halykbank.kkbsign.KKBSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

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


    String createPaymentOrderXML(BigDecimal amount, String cardholderName, String cvc, String desc,
                                 String month, String orderid, String pan, int trtype, String year) {

        String concatString = orderid + amount + currency + trtype + pan + merchantid;
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
        String xml = String.format("      class=\"collapse in\"\n" +
                        "      <soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                        "          <soapenv:Body>\n" +
                        "              <ns4:paymentOrderAcs xmlns:ns4=\"http://ws.epay.kkb.kz/xsd\">\n" +
                        "                  <order>\n" +
                        "                      <md>%s</md>\n" +
                        "                      <pares>%s</pares>\n" +
                        "                      <sessionid>%s</sessionid>\n" +
                        "                  </order>\n" +
                        "                  <requestSignature>\n" +
                        "                      <merchantCertificate>%s</merchantCertificate>\n" +
                        "                      <merchantId>%s</merchantId>\n" +
                        "                      <signatureValue>%s</signatureValue>\n" +
                        "                  </requestSignature>\n" +
                        "              </ns4:paymentOrderAcs>\n" +
                        "          </soapenv:Body>\n" +
                        "      </soapenv:Envelope>",
                md, pares, sessionid, merchantCertificate, merchantid, signatureValue
        );
        return xml;
    }


    @PostConstruct
    public void testCreateXML() {
        try {
            String xml = createPaymentOrderAcsXML("ASDEF8009001",
                    "ABCD-AMOUNT5-TERMINAL92061101-ORDER4785514--OK",
                    "1285268A80D266BB2E74AC1FE69D9D1E");
            logger.info("\n" + xml + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
