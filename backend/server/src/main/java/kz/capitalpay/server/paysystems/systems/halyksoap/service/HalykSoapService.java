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
                        "          <soapenv:Body>" +
                        "              <ns4:paymentOrder xmlns:ns4=\"http://ws.epay.kkb.kz/xsd\">" +
                        "                  <order>" +
                        "                      <amount>%s</amount>\n" +
                        "                      <cardholderName>%s</cardholderName>\n" +
                        "                      <currency>%s</currency>\n" +
                        "                      <cvc>%s</cvc>\n" +
                        "                      <desc>%s</desc>\n" +
                        "                      <merchantid>%s</merchantid>\n" +
                        "                      <month>%s</month>\n" +
                        "                      <orderid>%s</orderid>\n" +
                        "                      <pan>%s</pan>\n" +
                        "                      <trtype>%d</trtype>\n" +
                        "                      <year>%s</year>\n" +
                        "                  </order>\n" +
                        "                  <requestSignature>\n" +
                        "                      <merchantCertificate>%s</merchantCertificate>\n" +
                        "                      <merchantId>%s</merchantId>\n" +
                        "                      <signatureValue>%s</signatureValue>\n" +
                        "                  </requestSignature>\n" +
                        "              </ns4:paymentOrder>\n" +
                        "          </soapenv:Body>\n" +
                        "      </soapenv:Envelope>",
                amount.toString().replace(".", ","),
                cardholderName,
                currency,
                cvc,
                desc,
                merchantid,
                month,
                orderid,
                pan,
                trtype,
                year,
                merchantCertificate,
                merchantid,
                signatureValue
        );
        return xml;

    }


    @PostConstruct
    public void testCreateXML() {
        String xml = createPaymentOrderXML(new BigDecimal("5"), "Sergey Frolov", "653",
                "desc", "09", "101", "4405645000006150", 0, "15");
        logger.info("\n" + xml + "\n");
    }
}
