package kz.capitalpay.server.paysystems.systems.halyksoap.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.payments.dto.SendP2pToClientDto;
import kz.capitalpay.server.payments.model.CheckCardValidityPayment;
import kz.capitalpay.server.payments.model.P2pPayment;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.repository.CheckCardValidityPaymentRepository;
import kz.capitalpay.server.payments.repository.P2pPaymentRepository;
import kz.capitalpay.server.payments.service.P2pPaymentLogService;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.HalykTransferOrderDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.kkbsign.KKBSign;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykCheckOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykPaymentOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykPaymentOrderAcs;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykCheckOrderRepository;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykPaymentOrderAcsRepository;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykPaymentOrderRepository;
import kz.capitalpay.server.usercard.dto.CardDataResponseDto;
import kz.capitalpay.server.wsdl.*;
import org.apache.axis2.AxisFault;
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
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static kz.capitalpay.server.simple.service.SimpleService.*;

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

    @Autowired
    CheckCardValidityPaymentRepository checkCardValidityPaymentRepository;

    @Autowired
    P2pPaymentRepository p2pPaymentRepository;

    @Autowired
    P2pPaymentLogService p2pPaymentLogService;

    private String createTransferOrder(HalykTransferOrderDTO paymentOrder, String cvc, String month, String year, String pan) {
        String concatString = paymentOrder.getOrderid() + paymentOrder.getAmount() + paymentOrder.getCurrency() +
                paymentOrder.getTrtype() + pan + paymentOrder.getMerchantid();
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);
        String transferXml = String.format("<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
                        "<soapenv:Body>" +
                        "<ns4:transferOrder xmlns:ns4=\"http://ws.epay.kkb.kz/xsd\">" +
                        "<transferOrder xmlns:ns2=\"http://models.ws.epay.kkb.kz/xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TransferOrder\">" +
                        "<amount>%s</amount>" +
                        "<currency>%s</currency>" +
                        "<cvc>%s</cvc>" +
                        "<desc>%s</desc>" +
                        "<merchantid>%s</merchantid>" +
                        "<month>%s</month>" +
                        "<orderid>%s</orderid>" +
                        "<pan>%s</pan>" +
                        "<year>%s</year>" +
                        "<trtype>%d</trtype>" +
                        "<paymentto>%s</paymentto>" +
                        "<fromName>%s</fromName>" +
                        "<toName>%s</toName>" +
                        "<fromAddress>%s</fromAddress>" +
                        "<toAddress>%s</toAddress>" +
                        "</transferOrder>" +
                        "<requestSignature>" +
                        "<merchantCertificate>%s</merchantCertificate>" +
                        "<merchantId>%s</merchantId>" +
                        "<signatureValue>%s</signatureValue>" +
                        "</requestSignature>" +
                        "</ns4:transferOrder>" +
                        "</soapenv:Body>" +
                        "</soapenv:Envelope>", paymentOrder.getAmount(), paymentOrder.getCurrency(), cvc, paymentOrder.getDesc(),
                paymentOrder.getMerchantid(), month, paymentOrder.getOrderid(), pan, year, paymentOrder.getTrtype(), paymentOrder.getPaymentTo(),
                paymentOrder.getFromName(), paymentOrder.getToName(), paymentOrder.getFromAddress(), paymentOrder.getToAddress(), merchantCertificate, merchantid, signatureValue);
        return transferXml;
    }


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

            if ("111".equals(cvc)) {
                return "FAIL";
            }
            if (paymentOrder.getReturnCode() != null && paymentOrder.getReturnCode().equals("00")) {
                logger.info("Code 00, order: {}", gson.toJson(paymentOrder));
                paymentService.setStatusByPaySysPayId(paymentOrder.getOrderid(), SUCCESS);
            } else {
                if (paymentOrder.getPareq() != null && paymentOrder.getMd() != null) {

                    Map<String, String> param = new HashMap<>();
                    param.put("acsUrl", paymentOrder.getAcsUrl());
                    param.put("MD", paymentOrder.getMd());
                    param.put("PaReq", paymentOrder.getPareq());
                    logger.info("Code 00, order: {}", gson.toJson(paymentOrder));
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

    public boolean checkCardValidity(String ipAddress, String userAgent, CardDataResponseDto cardData) {
        CheckCardValidityPayment payment = generateCardCheckPayment(ipAddress, userAgent, Long.parseLong(merchantid),
                new BigDecimal(10));
        String orderId = payment.getOrderId();
        String pan = cardData.getCardNumber();
        String trType = "0";
        String year = cardData.getExpireYear().substring(2);
        String month = cardData.getExpireMonth();
        String cvv2 = cardData.getCvv2Code();
        String amount = "10";
        String currency = "KZT";

        EpayServiceStub.PaymentOrderResponse paymentOrderResponse = sendPaymentOrderRequest(amount, currency,
                cvv2, merchantid, month, year, orderId, pan, trType);

        logger.info("paymentOrderResponse");
        logger.info("Message: " + paymentOrderResponse.get_return().getMessage());
        logger.info("Approval code: " + paymentOrderResponse.get_return().getApprovalcode());
        logger.info("OrderId: " + paymentOrderResponse.get_return().getOrderid());
        logger.info("getReference: " + paymentOrderResponse.get_return().getReference());
        logger.info("getReference: " + paymentOrderResponse.get_return().getReturnCode());

        if (paymentOrderResponse.get_return().getReturnCode().equals("00")) {
            String reference = paymentOrderResponse.get_return().getReference();
            EpayServiceStub.ControlOrderForCommerceResponse controlOrderForCommerceResponse = sendControlOrderForCommerceRequest(amount,
                    currency, merchantid, orderId, reference, "22");
            logger.info("controlOrderForCommerceResponse");
            logger.info("Message: " + controlOrderForCommerceResponse.get_return().getMessage());
            logger.info("Approval code: " + controlOrderForCommerceResponse.get_return().getApprovalcode());
            logger.info("OrderId: " + controlOrderForCommerceResponse.get_return().getOrderid());
            logger.info("getReference: " + controlOrderForCommerceResponse.get_return().getReference());
            logger.info("getReference: " + controlOrderForCommerceResponse.get_return().getReturnCode());
            if (controlOrderForCommerceResponse.get_return().getReturnCode().equals("00")) {
                payment.setStatus("COMPLETED");
                checkCardValidityPaymentRepository.save(payment);
            }

            return true;
        }
        return false;
    }

    public boolean sendP2ToClient(String ipAddress, String userAgent, CardDataResponseDto merchantCardData, SendP2pToClientDto dto,
                                  String paymentToPan) {
        P2pPayment payment = generateP2pPayment(ipAddress, userAgent, dto.getMerchantId(), dto.getAcceptedSum(), dto.getCashBoxId());
        String orderId = payment.getOrderId();
        String pan = merchantCardData.getCardNumber();
        String year = merchantCardData.getExpireYear().substring(2);
        String month = merchantCardData.getExpireMonth();
        String cvv2 = merchantCardData.getCvv2Code();
        String amount = dto.getAcceptedSum().toString().replace(".", ",");
        String currency = "KZT";

        EpayServiceStub.TransferOrderResponse transferOrderResponse = sendTransferOrderRequest(amount, currency, cvv2,
                merchantid, month, year, orderId, pan, paymentToPan);

        if (transferOrderResponse.get_return().getReturnCode().equals("00")) {
            payment.setStatus(SUCCESS);
            p2pPaymentLogService.newEvent(payment.getGuid(), ipAddress, SUCCESS, gson.toJson(payment));
            p2pPaymentRepository.save(payment);
            return true;
        }
        return false;
    }

    private EpayServiceStub.PaymentOrderResponse sendPaymentOrderRequest(String amount, String currency, String cvv2,
                                                                         String requestMerchantId, String month, String year,
                                                                         String orderId, String pan, String trType) {
        EpayServiceStub stub = null;
        try {
            stub = new EpayServiceStub();
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
        EpayServiceStub.PaymentOrder paymentOrder = new EpayServiceStub.PaymentOrder();
        EpayServiceStub.Order order = new EpayServiceStub.Order();
        order.setAmount(amount);
        order.setCardholderName("test");
        order.setCurrency(currency);
        order.setCvc(cvv2);
        order.setDesc("Check card validity payment");
        order.setMerchantid(requestMerchantId);
        order.setMonth(month);
        order.setOrderid(orderId);
        order.setPan(pan);
        order.setTrtype(trType);
        order.setYear(year);

        String concatString = orderId + amount + currency +
                trType + pan + requestMerchantId;
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);

        paymentOrder.setOrder(order);
        EpayServiceStub.RequestSignature signature = new EpayServiceStub.RequestSignature();
        signature.setMerchantCertificate(merchantCertificate);
        signature.setMerchantId(requestMerchantId);
        signature.setSignatureValue(signatureValue);
        paymentOrder.setRequestSignature(signature);
        EpayServiceStub.PaymentOrderResponse response = null;
        try {
            response = stub.paymentOrder(paymentOrder);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return response;
    }

    private EpayServiceStub.ControlOrderForCommerceResponse sendControlOrderForCommerceRequest(String amount,
                                                                                               String currency,
                                                                                               String requestMerchantId,
                                                                                               String orderId,
                                                                                               String reference,
                                                                                               String trType) {
        EpayServiceStub stub = null;
        try {
            stub = new EpayServiceStub();
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
        EpayServiceStub.ControlOrderForCommerce controlOrderForCommerce = new EpayServiceStub.ControlOrderForCommerce();
        EpayServiceStub.ControlOrder controlOrder = new EpayServiceStub.ControlOrder();
        controlOrder.setAmount(amount);
        controlOrder.setCurrency(currency);
        controlOrder.setIntreference("test");
        controlOrder.setMerchantid(requestMerchantId);
        controlOrder.setOrderid(orderId);
        controlOrder.setReference(reference);
        controlOrder.setTrtype(trType);


        String concatString = orderId + amount + currency +
                trType + requestMerchantId;
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);

        controlOrderForCommerce.setControlOrder(controlOrder);
        EpayServiceStub.RequestSignature signature = new EpayServiceStub.RequestSignature();
        signature.setMerchantCertificate(merchantCertificate);
        signature.setMerchantId(requestMerchantId);
        signature.setSignatureValue(signatureValue);
        controlOrderForCommerce.setRequestSignature(signature);
        EpayServiceStub.ControlOrderForCommerceResponse response = null;
        try {
            response = stub.controlOrderForCommerce(controlOrderForCommerce);
        } catch (RemoteException | EpayServiceSAXExceptionException | EpayServiceParserConfigurationExceptionException | EpayServiceSignatureExceptionException | EpayServiceCertificateExceptionException | EpayServiceUnrecoverableKeyExceptionException | EpayServiceKeyStoreExceptionException | EpayServiceIOExceptionException | EpayServiceNoSuchAlgorithmExceptionException | EpayServiceInvalidKeyExceptionException e) {
            e.printStackTrace();
        }

        return response;
    }

    private EpayServiceStub.TransferOrderResponse sendTransferOrderRequest(String amount, String currency, String cvv2,
                                                                           String requestMerchantId, String month, String year,
                                                                           String orderId, String senderPan, String paymenToPan) {
        EpayServiceStub stub = null;
        try {
            stub = new EpayServiceStub();
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
        final String trType = "8";
        EpayServiceStub.TransferOrderE transferOrderE = new EpayServiceStub.TransferOrderE();
        EpayServiceStub.TransferOrder transferOrder = new EpayServiceStub.TransferOrder();
        transferOrder.setAmount(amount);
        transferOrder.setCurrency(currency);
        transferOrder.setCvc(cvv2);
        transferOrder.setDesc(" ");
        transferOrder.setMerchantid(merchantid);
        transferOrder.setMonth(month);
        transferOrder.setTrtype(trType);
        transferOrder.setOrderid(orderId);
        transferOrder.setPan(senderPan);
        transferOrder.setYear(year);
        transferOrder.setPaymentto(paymenToPan);

        String concatString = orderId + amount + currency +
                trType + senderPan + requestMerchantId;
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);

        transferOrderE.setTransferOrder(transferOrder);
        EpayServiceStub.RequestSignature signature = new EpayServiceStub.RequestSignature();
        signature.setMerchantCertificate(merchantCertificate);
        signature.setMerchantId(requestMerchantId);
        signature.setSignatureValue(signatureValue);
        transferOrderE.setRequestSignature(signature);
        EpayServiceStub.TransferOrderResponse response = null;
        try {
            response = stub.transferOrder(transferOrderE);
        } catch (RemoteException | EpayServiceSAXExceptionException | EpayServiceParserConfigurationExceptionException | EpayServiceSignatureExceptionException | EpayServiceCertificateExceptionException | EpayServiceUnrecoverableKeyExceptionException | EpayServiceKeyStoreExceptionException | EpayServiceIOExceptionException | EpayServiceNoSuchAlgorithmExceptionException | EpayServiceInvalidKeyExceptionException e) {
            e.printStackTrace();
        }

        return response;
    }

    private CheckCardValidityPayment generateCardCheckPayment(String ipAddress, String userAgent, Long merchantId,
                                                              BigDecimal totalAmount) {
        CheckCardValidityPayment payment = new CheckCardValidityPayment();
        CheckCardValidityPayment lastPayment = checkCardValidityPaymentRepository.findLast().orElse(null);
        if (Objects.nonNull(lastPayment)) {
            payment.setOrderId(generateOrderId(lastPayment.getOrderId()));
        } else {
            payment.setOrderId("00000163601600");
        }
        payment.setCurrency("KZT");
        payment.setDescription("Check card validity payment");
        payment.setLocalDateTime(LocalDateTime.now());
        payment.setIpAddress(ipAddress);
        payment.setMerchantId(merchantId);

        payment.setTotalAmount(totalAmount);
        payment.setUserAgent(userAgent);
        payment.setStatus("NEW");
        payment = checkCardValidityPaymentRepository.save(payment);
        return payment;
    }

    private P2pPayment generateP2pPayment(String ipAddress, String userAgent, Long merchantId,
                                          BigDecimal totalAmount, Long cashBoxId) {
        P2pPayment payment = new P2pPayment();
        P2pPayment lastPayment = p2pPaymentRepository.findLast().orElse(null);
        if (Objects.nonNull(lastPayment)) {
            payment.setOrderId(generateOrderId(lastPayment.getOrderId()));
        } else {
            payment.setOrderId("00000163801600");
        }
        payment.setCurrency("KZT");
        payment.setLocalDateTime(LocalDateTime.now());
        payment.setIpAddress(ipAddress);
        payment.setCashboxId(cashBoxId);
        payment.setMerchantId(merchantId);

        payment.setTotalAmount(totalAmount);
        payment.setUserAgent(userAgent);
        payment.setStatus(NEW_PAYMENT);
        payment = p2pPaymentRepository.save(payment);
        p2pPaymentLogService.newEvent(payment.getGuid(), ipAddress, NEW_PAYMENT, gson.toJson(payment));
        return payment;
    }

    private String generateOrderId(String lastOrderId) {
        Long lastOrderIdLong = Long.parseLong(lastOrderId);
        Long newOrderId = lastOrderIdLong + 1;
        StringBuilder zerosStr = new StringBuilder();
        if (String.valueOf(newOrderId).length() < 14) {
            zerosStr.append("0".repeat(Math.max(0, 14 - String.valueOf(newOrderId).length())));
        }
        return zerosStr.toString() + newOrderId;
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

            HalykPaymentOrder paymentOrder = getPaymentOrderBySession(sessionid);

            paymentOrderAcs.setOrderid(paymentOrder.getOrderid());
            paymentOrderAcs.setPareq(paymentOrder.getPareq());
            paymentOrderAcs.setAcsUrl(paymentOrder.getAcsUrl());

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

    private HalykPaymentOrder getPaymentOrderBySession(String sessionid) {

        return halykPaymentOrderRepository.findTopBySessionid(sessionid);

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
