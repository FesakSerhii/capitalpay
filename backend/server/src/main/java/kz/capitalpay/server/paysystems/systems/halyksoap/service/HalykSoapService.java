package kz.capitalpay.server.paysystems.systems.halyksoap.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.systems.halyksoap.kkbsign.KKBSign;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykCheckOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykPaymentOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykPaymentOrderAcs;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykCheckOrderRepository;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykPaymentOrderAcsRepository;
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

import static kz.capitalpay.server.simple.service.SimpleService.PENDING;
import static kz.capitalpay.server.simple.service.SimpleService.SUCCESS;

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

    @Value("${halyk.soap.client.alias}")
    String clientAlias;

    @Value("${halyk.soap.bank.alias}")
    String bankAlias;

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

    @Autowired
    HalykCheckOrderRepository halykCheckOrderRepository;

    @Autowired
    HalykPaymentOrderAcsRepository halykPaymentOrderAcsRepository;

    @Autowired
    PaymentService paymentService;


    private String createPaymentOrderXML(HalykPaymentOrder paymentOrder, String cvc, String month, String year, String pan) {

        String concatString = paymentOrder.getOrderid() + paymentOrder.getAmount() + paymentOrder.getCurrency() +
                paymentOrder.getTrtype() + pan + paymentOrder.getMerchantid();
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);
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
        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);
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

    public String paymentOrder(BigDecimal amount, String cardholderName, String cvc, String desc,
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

            if (paymentOrder.getReturnCode() != null && paymentOrder.getReturnCode().equals("00")) {
                paymentService.setStatusByPaySysPayId(paymentOrder.getOrderid(), SUCCESS);
            } else {
                if (paymentOrder.getPareq() != null && paymentOrder.getMd() != null) {
                    // TODO: костыль только на время отладки в песочнице
//                    if (sendOrderActionLink.equals("https://testpay.kkb.kz")) {
//                        paymentOrder.setMd(paymentOrder.getSessionid());
//                        halykPaymentOrderRepository.save(paymentOrder);
//                    }

                    Map<String, String> param = new HashMap<>();
                    param.put("acsUrl", paymentOrder.getAcsUrl());
                    param.put("MD", paymentOrder.getMd());
                    param.put("PaReq", paymentOrder.getPareq());
                    paymentService.setStatusByPaySysPayId(paymentOrder.getOrderid(), PENDING);
                    return gson.toJson(param);
                }
            }

            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "FAIL";
    }


    public String checkOrder(String orderid) {
        try {
            HalykCheckOrder checkOrder = new HalykCheckOrder();
            checkOrder.setTimestamp(System.currentTimeMillis());
            checkOrder.setLocalDateTime(LocalDateTime.now());
            checkOrder.setMerchantid(merchantid);
            checkOrder.setOrderid(orderid);

            halykCheckOrderRepository.save(checkOrder);

            String signedXML = createCheckOrderXML(checkOrder);
            Map<String, String> vars = new HashMap<>();
            vars.put("signedXML", signedXML);

            String response = restTemplate.postForObject(sendOrderActionLink + "/axis2/services/EpayService.EpayServiceHttpSoap12Endpoint/",
                    signedXML, String.class, java.util.Optional.ofNullable(null));
            logger.info("response: {}", response);

            parseCheckOrderResponse(checkOrder, response);
            logger.info(gson.toJson(checkOrder));
            return checkOrder.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HalykCheckOrder parseCheckOrderResponse(HalykCheckOrder checkOrder, String response) {
        try {
            Document xmlDoc = DocumentHelper.createDocument();

            xmlDoc = DocumentHelper.parseText(response);
            logger.info("Full Document {}", xmlDoc.asXML());

            xmlDoc.getRootElement().addNamespace("ns", "http://ws.epay.kkb.kz/xsd");
            Element AcceptReversalDate = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/acceptReversalDate");
            if (!AcceptReversalDate.getText().equals("null"))
                checkOrder.setAcceptReversalDate(AcceptReversalDate.getText());
            Element Approvalcode = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/approvalcode");
            if (!Approvalcode.getText().equals("null")) checkOrder.setApprovalcode(Approvalcode.getText());
            Element Cardhash = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/cardhash");
            if (!Cardhash.getText().equals("null")) checkOrder.setCardhash(Cardhash.getText());
            Element Intreference = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/intreference");
            if (!Intreference.getText().equals("null")) checkOrder.setIntreference(Intreference.getText());
            Element Message = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/message");
            if (!Message.getText().equals("null")) checkOrder.setMessage(Message.getText());
            Element Orderal = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/orderal");
            if (!Orderal.getText().equals("null")) checkOrder.setOrderal(Orderal.getText());
            Element PaidAmount = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/paidAmount");
            if (!PaidAmount.getText().equals("null")) checkOrder.setPaidAmount(PaidAmount.getText());
            Element PaidCurrency = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/paidCurrency");
            if (!PaidCurrency.getText().equals("null")) checkOrder.setPaidCurrency(PaidCurrency.getText());
            Element Payerip = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/payerip");
            if (!Payerip.getText().equals("null")) checkOrder.setPayerip(Payerip.getText());
            Element Payermail = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/payermail");
            if (!Payermail.getText().equals("null")) checkOrder.setPayermail(Payermail.getText());
            Element Payername = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/payername");
            if (!Payername.getText().equals("null")) checkOrder.setPayername(Payername.getText());
            Element Payerphone = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/payerphone");
            if (!Payerphone.getText().equals("null")) checkOrder.setPayerphone(Payerphone.getText());
            Element Reference = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/reference");
            if (!Reference.getText().equals("null")) checkOrder.setReference(Reference.getText());
            Element RefundTotalAmount = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/refundTotalAmount");
            if (!RefundTotalAmount.getText().equals("null"))
                checkOrder.setRefundTotalAmount(RefundTotalAmount.getText());
            Element Resultcode = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/resultcode");
            if (!Resultcode.getText().equals("null")) checkOrder.setResultcode(Resultcode.getText());
            Element Secure = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/secure");
            if (!Secure.getText().equals("null")) checkOrder.setSecure(Secure.getText());
            Element SessionDate = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/sessionDate");
            if (!SessionDate.getText().equals("null")) checkOrder.setSessionDate(SessionDate.getText());
            Element SessionId = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/sessionId");
            if (!SessionId.getText().equals("null")) checkOrder.setSessionId(SessionId.getText());
            Element Status = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/status");
            if (!Status.getText().equals("null")) checkOrder.setStatus(Status.getText());
            Element TransactionDate = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/transactionDate");
            if (!TransactionDate.getText().equals("null")) checkOrder.setTransactionDate(TransactionDate.getText());

            Element SignatureValue = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/responseSignature/signatureValue");
            String signatureValue = SignatureValue.getText() + "";

            Element SignedString = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:checkOrderResponse/return/responseSignature/signedString");
            String signedString = SignedString.getText() + "";
            logger.info("SignedString: {}", signedString);
            KKBSign kkbSign = new KKBSign();
            boolean signatureValid = kkbSign.verify(signedString, signatureValue, keystore, bankAlias, storepass); /// keypass???
            logger.info("Verify: {}", signatureValid);
            checkOrder.setSignatureValid(signatureValid);

            halykCheckOrderRepository.save(checkOrder);

            return checkOrder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createCheckOrderXML(HalykCheckOrder checkOrder) {

        String concatString = checkOrder.getMerchantid() + checkOrder.getOrderid();
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);
        String xml = String.format("<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
                        "<soapenv:Body>" +
                        "<ns4:checkOrder xmlns:ns4=\"http://ws.epay.kkb.kz/xsd\">" +
                        "<order>" +
                        "<merchantid>%s</merchantid>" +
                        "<orderid>%s</orderid>" +
                        "</order>" +
                        "<requestSignature>" +
                        "<merchantCertificate>%s</merchantCertificate>" +
                        "<merchantId>%s</merchantId>" +
                        "<signatureValue>%s</signatureValue>" +
                        "</requestSignature>" +
                        "</ns4:checkOrder>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>",
                checkOrder.getMerchantid(), checkOrder.getOrderid(),
                merchantCertificate, merchantid, signatureValue);
        return xml;

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

            Element SignatureValue = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/responseSignature/signatureValue");
            String signatureValue = SignatureValue.getText() + "";

            Element SignedString = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/responseSignature/signedString");
            String signedString = SignedString.getText() + "";
            logger.info("SignedString: {}", signedString);
            KKBSign kkbSign = new KKBSign();
            boolean signatureValid = kkbSign.verify(signedString, signatureValue, keystore, bankAlias, storepass); /// keypass???
            logger.info("Verify: {}", signatureValid);
            paymentOrder.setSignatureValid(signatureValid);

            halykPaymentOrderRepository.save(paymentOrder);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return paymentOrder;

    }

    public Payment paymentOrderAcs(String md, String pares, String sessionid) {
        try {
            HalykPaymentOrderAcs paymentOrderAcs = new HalykPaymentOrderAcs();
            paymentOrderAcs.setTimestamp(System.currentTimeMillis());
            paymentOrderAcs.setLocalDateTime(LocalDateTime.now());
            paymentOrderAcs.setMd(md);
            paymentOrderAcs.setPares(pares);
            paymentOrderAcs.setSessionid(sessionid);

            halykPaymentOrderAcsRepository.save(paymentOrderAcs);

            String signedXML = createPaymentOrderAcsXML(paymentOrderAcs);
            Map<String, String> vars = new HashMap<>();
            vars.put("signedXML", signedXML);

            String response = restTemplate.postForObject(sendOrderActionLink + "/axis2/services/EpayService.EpayServiceHttpSoap12Endpoint/",
                    signedXML, String.class, java.util.Optional.ofNullable(null));
            logger.info("response: {}", response);

            parsePaymentOrderAcsResponse(paymentOrderAcs, response);
            logger.info(gson.toJson(paymentOrderAcs));
            if (paymentOrderAcs.getReturnCode() != null && paymentOrderAcs.getReturnCode().equals("00")) {
                logger.info("Return code: {}", paymentOrderAcs.getReturnCode());
                Payment payment = paymentService.setStatusByPaySysPayId(paymentOrderAcs.getOrderid(), SUCCESS);
                return payment;
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HalykPaymentOrderAcs parsePaymentOrderAcsResponse(HalykPaymentOrderAcs paymentOrderAcs, String response) {
        try {
            Document xmlDoc = DocumentHelper.createDocument();

            xmlDoc = DocumentHelper.parseText(response);
            logger.info("Full Document {}", xmlDoc.asXML());

            xmlDoc.getRootElement().addNamespace("ns", "http://ws.epay.kkb.kz/xsd");
            Element AscUrl = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/acsUrl");
            if (!AscUrl.getText().equals("null")) paymentOrderAcs.setAcsUrl(AscUrl.getText());
            Element Approvalcode = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/approvalcode");
            if (!Approvalcode.getText().equals("null")) paymentOrderAcs.setApprovalcode(Approvalcode.getText());
            Element Intreference = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/intreference");
            if (!Intreference.getText().equals("null")) paymentOrderAcs.setIntreference(Intreference.getText());
            Element Is3ds = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/is3ds");
            if (!Is3ds.getText().equals("null")) paymentOrderAcs.setIs3ds(Is3ds.getText().equals("true"));
//            Element MD = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/md");
//            if (!MD.getText().equals("null")) paymentOrderAcs.setMd(MD.getText());
            Element Message = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/message");
            if (!Message.getText().equals("null")) paymentOrderAcs.setMessage(Message.getText());

            Element Orderid = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/orderid");
            if (!Orderid.getText().equals("null")) paymentOrderAcs.setOrderid(Orderid.getText());
            Element Pareq = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/pareq");
            if (!Pareq.getText().equals("null")) paymentOrderAcs.setPareq(Pareq.getText());
            Element Reference = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/reference");
            if (!Reference.getText().equals("null")) paymentOrderAcs.setReference(Reference.getText());
            Element ReturnCode = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/returnCode");
            if (!ReturnCode.getText().equals("null")) paymentOrderAcs.setReturnCode(ReturnCode.getText());
//            Element Sessionid = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/sessionid");
//            if (!Sessionid.getText().equals("null")) paymentOrderAcs.setSessionid(Sessionid.getText());
            Element TermUrl = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/termUrl");
            if (!TermUrl.getText().equals("null")) paymentOrderAcs.setTermUrl(TermUrl.getText());

            Element SignatureValue = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/responseSignature/signatureValue");
            String signatureValue = SignatureValue.getText() + "";

            Element SignedString = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/responseSignature/signedString");
            String signedString = SignedString.getText() + "";
            logger.info("SignedString: {}", signedString);
            KKBSign kkbSign = new KKBSign();
            boolean signatureValid = kkbSign.verify(signedString, signatureValue, keystore, bankAlias, storepass); /// keypass???
            logger.info("Verify: {}", signatureValid);
            paymentOrderAcs.setSignatureValid(signatureValid);

            halykPaymentOrderAcsRepository.save(paymentOrderAcs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return paymentOrderAcs;

    }

    private String createPaymentOrderAcsXML(HalykPaymentOrderAcs paymentOrderAcs) {
        String concatString = paymentOrderAcs.getMd() + paymentOrderAcs.getPares() + paymentOrderAcs.getSessionid();
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);
        String xml = String.format("<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
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
                paymentOrderAcs.getMd(), paymentOrderAcs.getPares(), paymentOrderAcs.getSessionid(),
                merchantCertificate, merchantid, signatureValue);
        return xml;
    }


    @PostConstruct
    public void testCreateXML() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        Thread.sleep(1000);
//                        paymentOrder(new BigDecimal("5.00"), "OLEG IVANOFF", "323", "Test payment SOAP",
//                                "12", "0000000074234", "4003035000005378", "25");

//                        Thread.sleep(1000);
//                        paymentOrder(new BigDecimal("70.0"), "OLEG IVANOFF", "653", "Test payment SOAP",
//                                "A9", "0000000074244", "440564000006150", "25");

//                        paymentOrderAcs("ASDEF8009003","ABCD-AMOUNT5.00-TERMINAL92061102-ORDER0000000074234--OK",
//                                "3B9482631E42175E5B06C568DE0F1132");

//                        String status = checkOrder("0000000074244");
//                        logger.info("Status: {}", status);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSessionByMD(String md) {
        HalykPaymentOrder paymentOrder = halykPaymentOrderRepository.findTopByMd(md);
        return paymentOrder.getSessionid();

    }

    public Cashbox getCashboxByMD(String md) {
        HalykPaymentOrder paymentOrder = halykPaymentOrderRepository.findTopByMd(md);
        Cashbox cashbox = paymentService.getCashboxByOrderId(paymentOrder.getOrderid());
        return cashbox;
    }

    public Payment getPaymentByMd(String md) {
        HalykPaymentOrder paymentOrder = halykPaymentOrderRepository.findTopByMd(md);
        Payment payment = paymentService.getPaymentByOrderId(paymentOrder.getOrderid());
        return payment;
    }

    public String getSessionByPaRes(String paRes) {
        String pareq = paRes.replace("-OK", "");
        HalykPaymentOrder paymentOrder = halykPaymentOrderRepository.findTopByPareq(pareq);
        return paymentOrder.getSessionid();
    }

    public Payment getPaymentByPaRes(String paRes) {
        String pareq = paRes.replace("-OK", "");
        HalykPaymentOrder paymentOrder = halykPaymentOrderRepository.findTopByPareq(pareq);
        logger.info("PayOrder: {}", gson.toJson(paymentOrder));
        Payment payment = paymentService.getByPaySysPayId(paymentOrder.getOrderid());
        return payment;
    }
}
