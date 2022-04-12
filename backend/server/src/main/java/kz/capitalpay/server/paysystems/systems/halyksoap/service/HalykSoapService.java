package kz.capitalpay.server.paysystems.systems.halyksoap.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.p2p.dto.SendP2pToClientDto;
import kz.capitalpay.server.p2p.service.P2pPaymentService;
import kz.capitalpay.server.payments.model.CheckCardValidityPayment;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.repository.CheckCardValidityPaymentRepository;
import kz.capitalpay.server.payments.repository.PaymentRepository;
import kz.capitalpay.server.payments.service.PaymentLogService;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.systems.halyksoap.kkbsign.KKBSign;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykCheckOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykPaymentOrderAcs;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykCheckOrderRepository;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykOrderRepository;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykPaymentOrderAcsRepository;
import kz.capitalpay.server.usercard.dto.CardDataResponseDto;
import kz.capitalpay.server.usercard.dto.CheckCardValidityResponse;
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
    HalykOrderRepository halykOrderRepository;

    @Autowired
    HalykCheckOrderRepository halykCheckOrderRepository;

    @Autowired
    HalykPaymentOrderAcsRepository halykPaymentOrderAcsRepository;

    @Autowired
    PaymentService paymentService;

    @Autowired
    CheckCardValidityPaymentRepository checkCardValidityPaymentRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentLogService paymentLogService;

    @Autowired
    P2pPaymentService p2pPaymentService;


//    private String createTransferOrder(HalykOrderDTO paymentOrder, String cvc, String month, String year, String pan) {
//        String concatString = paymentOrder.getOrderid() + paymentOrder.getAmount() + paymentOrder.getCurrency() +
//                paymentOrder.getTrtype() + pan + paymentOrder.getMerchantid();
//        KKBSign kkbSign = new KKBSign();
//        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);
//        String transferXml = String.format("<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
//                        "<soapenv:Body>" +
//                        "<ns4:transferOrder xmlns:ns4=\"http://ws.epay.kkb.kz/xsd\">" +
//                        "<transferOrder xmlns:ns2=\"http://models.ws.epay.kkb.kz/xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:TransferOrder\">" +
//                        "<amount>%s</amount>" +
//                        "<currency>%s</currency>" +
//                        "<cvc>%s</cvc>" +
//                        "<desc>%s</desc>" +
//                        "<merchantid>%s</merchantid>" +
//                        "<month>%s</month>" +
//                        "<orderid>%s</orderid>" +
//                        "<pan>%s</pan>" +
//                        "<year>%s</year>" +
//                        "<trtype>%d</trtype>" +
//                        "<paymentto>%s</paymentto>" +
//                        "<fromName>%s</fromName>" +
//                        "<toName>%s</toName>" +
//                        "<fromAddress>%s</fromAddress>" +
//                        "<toAddress>%s</toAddress>" +
//                        "</transferOrder>" +
//                        "<requestSignature>" +
//                        "<merchantCertificate>%s</merchantCertificate>" +
//                        "<merchantId>%s</merchantId>" +
//                        "<signatureValue>%s</signatureValue>" +
//                        "</requestSignature>" +
//                        "</ns4:transferOrder>" +
//                        "</soapenv:Body>" +
//                        "</soapenv:Envelope>", paymentOrder.getAmount(), paymentOrder.getCurrency(), cvc, paymentOrder.getDesc(),
//                paymentOrder.getMerchantid(), month, paymentOrder.getOrderid(), pan, year, paymentOrder.getTrtype(), paymentOrder.getPaymentTo(),
//                paymentOrder.getFromName(), paymentOrder.getToName(), paymentOrder.getFromAddress(), paymentOrder.getToAddress(), merchantCertificate, merchantid, signatureValue);
//        return transferXml;
//    }


//    private String createPaymentOrderXML(HalykPaymentOrder paymentOrder, String cvc, String month, String year, String pan) {
//
//        String concatString = paymentOrder.getOrderid() + paymentOrder.getAmount() + paymentOrder.getCurrency() +
//                paymentOrder.getTrtype() + pan + paymentOrder.getMerchantid();
//        KKBSign kkbSign = new KKBSign();
//        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);
//        String xml = String.format("<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
//                        "<soapenv:Body>" +
//                        "<ns4:paymentOrder xmlns:ns4=\"http://ws.epay.kkb.kz/xsd\">" +
//                        "<order>" +
//                        "<amount>%s</amount>" +
//                        "<cardholderName>%s</cardholderName>" +
//                        "<currency>%s</currency>" +
//                        "<cvc>%s</cvc>" +
//                        "<desc>%s</desc>" +
//                        "<merchantid>%s</merchantid>" +
//                        "<month>%s</month>" +
//                        "<orderid>%s</orderid>" +
//                        "<pan>%s</pan>" +
//                        "<trtype>%d</trtype>" +
//                        "<year>%s</year>" +
//                        "</order>" +
//                        "<requestSignature>" +
//                        "<merchantCertificate>%s</merchantCertificate>" +
//                        "<merchantId>%s</merchantId>" +
//                        "<signatureValue>%s</signatureValue>" +
//                        "</requestSignature>" +
//                        "</ns4:paymentOrder>" +
//                        "</soapenv:Body>" +
//                        "</soapenv:Envelope>",
//                paymentOrder.getAmount(), paymentOrder.getCardholderName(), paymentOrder.getCurrency(), cvc,
//                paymentOrder.getDesc(), paymentOrder.getMerchantid(), month, paymentOrder.getOrderid(),
//                pan, paymentOrder.getTrtype(), year,
//                merchantCertificate, merchantid, signatureValue
//        );
//        return xml;
//    }

//    String createPaymentOrderAcsXML(String md, String pares, String sessionid) {
//        String concatString = md + pares + sessionid;
//        KKBSign kkbSign = new KKBSign();
//        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);
//        String xml = String.format(
//                "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
//                        "<soapenv:Body>" +
//                        "<ns4:paymentOrderAcs xmlns:ns4=\"http://ws.epay.kkb.kz/xsd\">" +
//                        "<order>" +
//                        "<md>%s</md>" +
//                        "<pares>%s</pares>" +
//                        "<sessionid>%s</sessionid>" +
//                        "</order>" +
//                        "<requestSignature>" +
//                        "<merchantCertificate>%s</merchantCertificate>" +
//                        "<merchantId>%s</merchantId>" +
//                        "<signatureValue>%s</signatureValue>" +
//                        "</requestSignature>" +
//                        "</ns4:paymentOrderAcs>" +
//                        "</soapenv:Body>" +
//                        "</soapenv:Envelope>",
//                md, pares, sessionid, merchantCertificate, merchantid, signatureValue
//        );
//        return xml;
//    }

//    public String paymentOrder(BigDecimal amount, String cardholderName, String cvc, String desc,
//                               String month, String orderId, String pan, String year, boolean p2p) {
//        try {
//            HalykPaymentOrder paymentOrder = generateHalykPaymentOrder(amount, cardholderName, desc, orderId);
//
//            year = year.length() > 2 ? year.substring(2) : year;
//
//            String signedXML = createPaymentOrderXML(paymentOrder, cvc, month, year, pan);
//            Map<String, String> vars = new HashMap<>();
//            vars.put("signedXML", signedXML);
//            logger.info("signedXML: {}", signedXML);
//
//            String response = restTemplate.postForObject(sendOrderActionLink + "/axis2/services/EpayService.EpayServiceHttpSoap12Endpoint/",
//                    signedXML, String.class, java.util.Optional.ofNullable(null));
//            logger.info("response: {}", response);
//
//            parsePaymentOrderResponse(paymentOrder, response);
//            logger.info(gson.toJson(paymentOrder));
//
//            if ("111".equals(cvc)) {
//                return "FAIL";
//            }
//            if (paymentOrder.getReturnCode() != null && paymentOrder.getReturnCode().equals("00")) {
//                logger.info("Code 00, order: {}", gson.toJson(paymentOrder));
//                if (p2p) {
//                    p2pPaymentService.setStatusByPaySysPayId(paymentOrder.getOrderid(), SUCCESS);
//                } else {
//                    paymentService.setStatusByPaySysPayId(paymentOrder.getOrderid(), SUCCESS);
//                }
//            } else {
//                if (paymentOrder.getPareq() != null && paymentOrder.getMd() != null) {
//                    Map<String, String> param = new HashMap<>();
//                    param.put("acsUrl", paymentOrder.getAcsUrl());
//                    param.put("MD", paymentOrder.getMd());
//                    param.put("PaReq", paymentOrder.getPareq());
//                    logger.info("Code 00, order: {}", gson.toJson(paymentOrder));
//                    if (p2p) {
//                        p2pPaymentService.setStatusByPaySysPayId(paymentOrder.getOrderid(), PENDING);
//                    } else {
//                        paymentService.setStatusByPaySysPayId(paymentOrder.getOrderid(), PENDING);
//                    }
//                    return gson.toJson(param);
//                }
//                if (p2p) {
//                    p2pPaymentService.setStatusByPaySysPayId(paymentOrder.getOrderid(), FAILED);
//                } else {
//                    paymentService.setStatusByPaySysPayId(paymentOrder.getOrderid(), FAILED);
//                }
//                return "FAIL";
//            }
//            return "OK";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "FAIL";
//    }

    public String getPaymentOrderResult(BigDecimal amount, String cardholderName, String cvc, String desc,
                                        String month, String orderId, String pan, String year) {
        try {
            HalykOrder paymentOrder = generateHalykOrder(amount, cardholderName, desc, orderId, 1);
            year = year.substring(2);

            EpayServiceStub.PaymentOrderResponse paymentOrderResponse = sendPaymentOrderRequest(
                    amount.toString(), currency, cvc, merchantid, month, year, orderId, pan, "1");

            EpayServiceStub.Result result = paymentOrderResponse.get_return();

            parsePaymentOrderResponse(paymentOrder, result);
            logger.info(gson.toJson(paymentOrder));
            logger.info("result.getReturnCode(): {}", result.getReturnCode());

            if ("111".equals(cvc)) {
                return "FAIL";
            }
            if (result.getReturnCode().equals("00")) {
                logger.info("paymentOrder.getOrderid(): {}", paymentOrder.getOrderid());
                logger.info("Code 00, order: {}", gson.toJson(paymentOrder));
                String reference = result.getReference();
                EpayServiceStub.ControlOrderForCommerceResponse controlOrderForCommerceResponse = sendControlOrderForCommerceRequest(amount.toString(),
                        currency, merchantid, orderId, reference, "22");
                logger.info("controlOrderForCommerceResponse");
                logger.info("Message: " + controlOrderForCommerceResponse.get_return().getMessage());
                logger.info("Approval code: " + controlOrderForCommerceResponse.get_return().getApprovalcode());
                logger.info("OrderId: " + controlOrderForCommerceResponse.get_return().getOrderid());
                logger.info("getReference: " + controlOrderForCommerceResponse.get_return().getReference());
                logger.info("PaReq: " + controlOrderForCommerceResponse.get_return().getPareq());
                logger.info("Md: " + controlOrderForCommerceResponse.get_return().getMd());
                logger.info("AcsUrl: " + controlOrderForCommerceResponse.get_return().getAcsUrl());

//                if (p2p) {
//                    p2pPaymentService.setStatusByPaySysPayId(paymentOrder.getOrderid(), SUCCESS);
//                } else {
                paymentService.setStatusByPaySysPayId(paymentOrder.getOrderid(), SUCCESS);
//                }
            } else {
                return check3ds(result, paymentOrder.getOrderid());
            }
            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "FAIL";
    }

    public CheckCardValidityResponse checkCardValidity(String ipAddress, String userAgent, CardDataResponseDto cardData) {
        CheckCardValidityPayment payment = generateCardCheckPayment(ipAddress, userAgent, Long.parseLong(merchantid),
                new BigDecimal(10));
        String orderId = payment.getOrderId();
        String pan = cardData.getCardNumber();
        String trType = "0";
        String year = cardData.getExpireYear().substring(2);
        String month = cardData.getExpireMonth();
        String cvv2 = cardData.getCvv2Code();
        String amount = "10";

        HalykOrder halykOrder = generateHalykOrder(new BigDecimal(amount),
                "", "Check card validity payment", payment.getOrderId(), 0);

        EpayServiceStub.PaymentOrderResponse paymentOrderResponse = sendPaymentOrderRequest(amount, currency,
                cvv2, merchantid, month, year, orderId, pan, trType);

        EpayServiceStub.Result result = paymentOrderResponse.get_return();
        parsePaymentOrderResponse(halykOrder, result);

        logger.info("paymentOrderResponse");
        logger.info("Message: " + result.getMessage());
        logger.info("Approval code: " + result.getApprovalcode());
        logger.info("OrderId: " + result.getOrderid());
        logger.info("getReference: " + result.getReference());
        logger.info("getReturnCode: " + result.getReturnCode());
        logger.info("PaReq: " + result.getPareq());
        logger.info("Md: " + result.getMd());
        logger.info("AcsUrl: " + result.getAcsUrl());

        if ((result.getReturnCode() == null || result.getReturnCode().equals("null"))
                && (result.getAcsUrl() != null || !result.getAcsUrl().equals("null"))
                && (result.getPareq() != null || !result.getPareq().equals("null"))) {
            return new CheckCardValidityResponse(true, result.getReturnCode());
        }
        if (result.getReturnCode().equals("00")) {
            String reference = result.getReference();
            EpayServiceStub.ControlOrderForCommerceResponse controlOrderForCommerceResponse = sendControlOrderForCommerceRequest(amount,
                    currency, merchantid, orderId, reference, "22");
            logger.info("controlOrderForCommerceResponse");
            logger.info("Message: " + controlOrderForCommerceResponse.get_return().getMessage());
            logger.info("Approval code: " + controlOrderForCommerceResponse.get_return().getApprovalcode());
            logger.info("OrderId: " + controlOrderForCommerceResponse.get_return().getOrderid());
            logger.info("getReference: " + controlOrderForCommerceResponse.get_return().getReference());
            logger.info("PaReq: " + controlOrderForCommerceResponse.get_return().getPareq());
            logger.info("Md: " + controlOrderForCommerceResponse.get_return().getMd());
            logger.info("AcsUrl: " + controlOrderForCommerceResponse.get_return().getAcsUrl());
            if (controlOrderForCommerceResponse.get_return().getReturnCode().equals("00")) {
                payment.setStatus("COMPLETED");
                checkCardValidityPaymentRepository.save(payment);
            }
            return new CheckCardValidityResponse(true, result.getReturnCode());
        }
        return new CheckCardValidityResponse(false, result.getReturnCode());
    }

    public String sendP2p(String ipAddress, String userAgent, CardDataResponseDto payerCardData, SendP2pToClientDto dto,
                          String paymentToPan, boolean toClient) {
        Payment payment = p2pPaymentService.generateP2pPayment(ipAddress, userAgent, dto.getMerchantId(),
                dto.getAcceptedSum(), dto.getCashBoxId(), toClient, currency, null);

        HalykOrder transferOrder = generateHalykOrder(dto.getAcceptedSum(),
                "", "p2p", payment.getPaySysPayId(), 8);

        String orderId = payment.getPaySysPayId();
        String pan = payerCardData.getCardNumber();
        String year = payerCardData.getExpireYear().substring(2);
        String month = payerCardData.getExpireMonth();
        String cvv2 = payerCardData.getCvv2Code();
        String amount = dto.getAcceptedSum().toString().replace(".", ",");

        EpayServiceStub.TransferOrderResponse transferOrderResponse = sendTransferOrderRequest(amount, currency, cvv2,
                merchantid, month, year, orderId, pan, paymentToPan);

        logger.info("transferOrderResponse");
        logger.info("Message: " + transferOrderResponse.get_return().getMessage());
        logger.info("Approval code: " + transferOrderResponse.get_return().getApprovalcode());
        logger.info("OrderId: " + transferOrderResponse.get_return().getOrderid());
        logger.info("getReference: " + transferOrderResponse.get_return().getReference());
        logger.info("getReturnCode: " + transferOrderResponse.get_return().getReturnCode());
        logger.info("AcsUrl: " + transferOrderResponse.get_return().getAcsUrl());
        logger.info("Md: " + transferOrderResponse.get_return().getMd());
        logger.info("PaReq: " + transferOrderResponse.get_return().getPareq());

        EpayServiceStub.Result result = transferOrderResponse.get_return();

        parseTransferOrderResponse(transferOrder, result);
        logger.info(gson.toJson(transferOrder));

        if (transferOrderResponse.get_return().getReturnCode().equals("00")) {
            payment.setStatus(SUCCESS);
            paymentLogService.newEvent(payment.getGuid(), ipAddress, SUCCESS, gson.toJson(payment));
            paymentRepository.save(payment);
            return "OK";
        }
        return check3ds(result, transferOrder.getOrderid());
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

        logger.info("amount: {}", amount);
        logger.info("cardholderName: {}", "test");
        logger.info("currency: {}", currency);
        logger.info("cvc: {}", cvv2);
        logger.info("desc: {}", "Check card validity payment");
        logger.info("merchantId: {}", requestMerchantId);
        logger.info("month: {}", month);
        logger.info("orderId: {}", orderId);
        logger.info("pan: {}", pan);
        logger.info("trType: {}", trType);
        logger.info("year: {}", year);

        String concatString = orderId + amount + currency +
                trType + pan + requestMerchantId;
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);

        logger.info("merchantCertificate {}", merchantCertificate);
        logger.info("signatureValue {}", signatureValue);

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

    private EpayServiceStub.PaymentOrderAcsResponse sendPaymentOrderAcsRequest(String pares, String MD, String sessionId) {
        EpayServiceStub stub = null;
        try {
            stub = new EpayServiceStub();
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
        EpayServiceStub.PaymentOrderAcs paymentOrderAcs = new EpayServiceStub.PaymentOrderAcs();
        EpayServiceStub.AcsOrder order = new EpayServiceStub.AcsOrder();
        order.setPares(pares);
        order.setMd(MD);
        order.setSessionid(sessionId);

        logger.info("sendPaymentOrderAcsRequest()");
        logger.info("pares: {}", pares);
        logger.info("MD: {}", MD);
        logger.info("sessionId: {}", sessionId);

        String concatString = MD + pares + sessionId;
        KKBSign kkbSign = new KKBSign();
        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);

        logger.info("merchantCertificate {}", merchantCertificate);
        logger.info("signatureValue {}", signatureValue);

        paymentOrderAcs.setOrder(order);
        EpayServiceStub.RequestSignature signature = new EpayServiceStub.RequestSignature();
        signature.setMerchantCertificate(merchantCertificate);
        signature.setMerchantId(merchantid);
        signature.setSignatureValue(signatureValue);
        paymentOrderAcs.setRequestSignature(signature);
        EpayServiceStub.PaymentOrderAcsResponse response = null;
        try {
            response = stub.paymentOrderAcs(paymentOrderAcs);
        } catch (RemoteException | EpayServiceSAXExceptionException | EpayServiceInvalidKeyExceptionException | EpayServiceSignatureExceptionException | EpayServiceCertificateExceptionException | EpayServiceUnrecoverableKeyExceptionException | EpayServiceKeyStoreExceptionException | EpayServiceIOExceptionException | EpayServiceNoSuchAlgorithmExceptionException | EpayServiceParserConfigurationExceptionException e) {
            e.printStackTrace();
        }

        return response;
    }

    private CheckCardValidityPayment generateCardCheckPayment(String ipAddress, String userAgent, Long merchantId,
                                                              BigDecimal totalAmount) {
        CheckCardValidityPayment payment = new CheckCardValidityPayment();
        CheckCardValidityPayment lastPayment = checkCardValidityPaymentRepository.findLast().orElse(null);
        if (Objects.nonNull(lastPayment)) {
            payment.setOrderId(p2pPaymentService.generateOrderId(lastPayment.getOrderId()));
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

    private String check3ds(EpayServiceStub.Result response, String orderId) {
        if (response.getPareq() != null && response.getMd() != null) {
            Map<String, String> param = new HashMap<>();
            param.put("acsUrl", response.getAcsUrl());
            param.put("MD", response.getMd());
            param.put("PaReq", response.getPareq());
            logger.info("Code 00, order: {}", gson.toJson(response));
//            if (p2p) {
//                p2pPaymentService.setStatusByPaySysPayId(orderId, PENDING);
//            } else {
            paymentService.setStatusByPaySysPayId(orderId, PENDING);
//            }
            return gson.toJson(param);
        }
//        if (p2p) {
//            p2pPaymentService.setStatusByPaySysPayId(orderId, FAILED);
//        } else {
        paymentService.setStatusByPaySysPayId(orderId, FAILED);
//        }
        return "FAIL";
    }

    private HalykOrder generateHalykOrder(BigDecimal amount, String cardholderName, String desc, String orderId, int trType) {
        HalykOrder paymentOrder = new HalykOrder();
        paymentOrder.setTimestamp(System.currentTimeMillis());
        paymentOrder.setLocalDateTime(LocalDateTime.now());
        paymentOrder.setAmount(amount.setScale(2).toString().replace(".", ","));
        paymentOrder.setCardholderName(cardholderName);
        paymentOrder.setCurrency(currency);
        paymentOrder.setDesc(desc);
        paymentOrder.setMerchantid(merchantid);
        paymentOrder.setOrderid(orderId);
        paymentOrder.setTrtype(trType);
        return halykOrderRepository.save(paymentOrder);
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

//    private HalykPaymentOrder parsePaymentOrderResponse(HalykPaymentOrder paymentOrder, String response) {
//        try {
//            Document xmlDoc = DocumentHelper.createDocument();
//
//            xmlDoc = DocumentHelper.parseText(response);
//            logger.info("Full Document {}", xmlDoc.asXML());
//
//            xmlDoc.getRootElement().addNamespace("ns", "http://ws.epay.kkb.kz/xsd");
//            Element AscUrl = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/acsUrl");
//            if (!AscUrl.getText().equals("null")) paymentOrder.setAcsUrl(AscUrl.getText());
//            Element Approvalcode = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/approvalcode");
//            if (!Approvalcode.getText().equals("null")) paymentOrder.setApprovalcode(Approvalcode.getText());
//            Element Intreference = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/intreference");
//            if (!Intreference.getText().equals("null")) paymentOrder.setIntreference(Intreference.getText());
//            Element Is3ds = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/is3ds");
//            if (!Is3ds.getText().equals("null")) paymentOrder.setIs3ds(Is3ds.getText().equals("true"));
//            Element MD = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/md");
//            if (!MD.getText().equals("null")) paymentOrder.setMd(MD.getText());
//            Element Message = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/message");
//            if (!Message.getText().equals("null")) paymentOrder.setMessage(Message.getText());
//            Element Pareq = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/pareq");
//            if (!Pareq.getText().equals("null")) paymentOrder.setPareq(Pareq.getText());
//            Element Reference = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/reference");
//            if (!Reference.getText().equals("null")) paymentOrder.setReference(Reference.getText());
//            Element ReturnCode = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/returnCode");
//            if (!ReturnCode.getText().equals("null")) paymentOrder.setReturnCode(ReturnCode.getText());
//            Element Sessionid = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/sessionid");
//            if (!Sessionid.getText().equals("null")) paymentOrder.setSessionid(Sessionid.getText());
//            Element TermUrl = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/termUrl");
//            if (!TermUrl.getText().equals("null")) paymentOrder.setTermUrl(TermUrl.getText());
//
//            Element SignatureValue = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/responseSignature/signatureValue");
//            String signatureValue = SignatureValue.getText() + "";
//
//            Element SignedString = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderResponse/return/responseSignature/signedString");
//            String signedString = SignedString.getText() + "";
//            logger.info("SignedString: {}", signedString);
//            KKBSign kkbSign = new KKBSign();
//            boolean signatureValid = kkbSign.verify(signedString, signatureValue, keystore, bankAlias, storepass); /// keypass???
//            logger.info("Verify: {}", signatureValid);
//            paymentOrder.setSignatureValid(signatureValid);
//
//            halykPaymentOrderRepository.save(paymentOrder);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return paymentOrder;
//
//    }

    private HalykOrder parsePaymentOrderResponse(HalykOrder paymentOrder, EpayServiceStub.Result response) {
        try {
            paymentOrder.setAcsUrl(response.getAcsUrl());
            paymentOrder.setApprovalcode(response.getApprovalcode());
            paymentOrder.setIntreference(response.getIntreference());
            paymentOrder.setIs3ds(response.getIs3Ds());
            paymentOrder.setMd(response.getMd());
            paymentOrder.setMessage(response.getMessage());
            paymentOrder.setPareq(response.getPareq());
            paymentOrder.setReference(response.getReference());
            paymentOrder.setReturnCode(response.getReturnCode());
            paymentOrder.setSessionid(response.getSessionid());
            paymentOrder.setTermUrl(response.getTermUrl());

            String signatureValue = response.getResponseSignature().getSignatureValue();
            String signedString = response.getResponseSignature().getSignedString();
            logger.info("SignedString: {}", signedString);
            KKBSign kkbSign = new KKBSign();
            boolean signatureValid = kkbSign.verify(signedString, signatureValue, keystore, bankAlias, storepass); /// keypass???
            logger.info("Verify: {}", signatureValid);
            paymentOrder.setSignatureValid(signatureValid);
            halykOrderRepository.save(paymentOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paymentOrder;
    }

    private HalykOrder parseTransferOrderResponse(HalykOrder transferOrder, EpayServiceStub.Result response) {
        try {
            transferOrder.setAcsUrl(response.getAcsUrl());
            transferOrder.setApprovalcode(response.getApprovalcode());
            transferOrder.setIntreference(response.getIntreference());
            transferOrder.setIs3ds(response.getIs3Ds());
            transferOrder.setMd(response.getMd());
            transferOrder.setMessage(response.getMessage());
            transferOrder.setPareq(response.getPareq());
            transferOrder.setReference(response.getReference());
            transferOrder.setReturnCode(response.getReturnCode());
            transferOrder.setSessionid(response.getSessionid());
            transferOrder.setTermUrl(response.getTermUrl());

            String signatureValue = response.getResponseSignature().getSignatureValue();
            String signedString = response.getResponseSignature().getSignedString();
            logger.info("SignedString: {}", signedString);
            KKBSign kkbSign = new KKBSign();
            boolean signatureValid = kkbSign.verify(signedString, signatureValue, keystore, bankAlias, storepass); /// keypass???
            logger.info("Verify: {}", signatureValid);
            transferOrder.setSignatureValid(signatureValid);
            halykOrderRepository.save(transferOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transferOrder;
    }

    public Payment paymentOrderAcs(String md, String pares, String sessionid) {
        try {
            HalykPaymentOrderAcs paymentOrderAcs = new HalykPaymentOrderAcs();
            paymentOrderAcs.setTimestamp(System.currentTimeMillis());
            paymentOrderAcs.setLocalDateTime(LocalDateTime.now());
            paymentOrderAcs.setMd(md);
            paymentOrderAcs.setPares(pares);
            paymentOrderAcs.setSessionid(sessionid);

            HalykOrder paymentOrder = getPaymentOrderBySession(sessionid);

            paymentOrderAcs.setOrderid(paymentOrder.getOrderid());
            paymentOrderAcs.setPareq(paymentOrder.getPareq());
            paymentOrderAcs.setAcsUrl(paymentOrder.getAcsUrl());

            halykPaymentOrderAcsRepository.save(paymentOrderAcs);

            EpayServiceStub.PaymentOrderAcsResponse paymentOrderAcsResponse = sendPaymentOrderAcsRequest(pares, md, sessionid);
            parsePaymentOrderAcsResponse(paymentOrderAcs, paymentOrderAcsResponse.get_return());

            logger.info(gson.toJson(paymentOrderAcs));
            if (paymentOrderAcs.getReturnCode() != null && paymentOrderAcs.getReturnCode().equals("00")) {
                logger.info("Return code: {}", paymentOrderAcs.getReturnCode());
//                if (p2p) {
//                    return p2pPaymentService.setStatusByPaySysPayId(paymentOrderAcs.getOrderid(), SUCCESS);
//                } else {
                return paymentService.setStatusByPaySysPayId(paymentOrderAcs.getOrderid(), SUCCESS);
//                }
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HalykOrder getPaymentOrderBySession(String sessionid) {
        return halykOrderRepository.findTopBySessionid(sessionid);
    }

//    private HalykPaymentOrderAcs parsePaymentOrderAcsResponse(HalykPaymentOrderAcs paymentOrderAcs, String response) {
//        try {
//            Document xmlDoc = DocumentHelper.createDocument();
//
//            xmlDoc = DocumentHelper.parseText(response);
//            logger.info("Full Document {}", xmlDoc.asXML());
//
//            xmlDoc.getRootElement().addNamespace("ns", "http://ws.epay.kkb.kz/xsd");
//            Element AscUrl = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/acsUrl");
//            if (!AscUrl.getText().equals("null")) paymentOrderAcs.setAcsUrl(AscUrl.getText());
//            Element Approvalcode = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/approvalcode");
//            if (!Approvalcode.getText().equals("null")) paymentOrderAcs.setApprovalcode(Approvalcode.getText());
//            Element Intreference = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/intreference");
//            if (!Intreference.getText().equals("null")) paymentOrderAcs.setIntreference(Intreference.getText());
//            Element Is3ds = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/is3ds");
//            if (!Is3ds.getText().equals("null")) paymentOrderAcs.setIs3ds(Is3ds.getText().equals("true"));
////            Element MD = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/md");
////            if (!MD.getText().equals("null")) paymentOrderAcs.setMd(MD.getText());
//            Element Message = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/message");
//            if (!Message.getText().equals("null")) paymentOrderAcs.setMessage(Message.getText());
//
//            Element Orderid = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/orderid");
//            if (!Orderid.getText().equals("null")) paymentOrderAcs.setOrderid(Orderid.getText());
//            Element Pareq = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/pareq");
//            if (!Pareq.getText().equals("null")) paymentOrderAcs.setPareq(Pareq.getText());
//            Element Reference = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/reference");
//            if (!Reference.getText().equals("null")) paymentOrderAcs.setReference(Reference.getText());
//            Element ReturnCode = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/returnCode");
//            if (!ReturnCode.getText().equals("null")) paymentOrderAcs.setReturnCode(ReturnCode.getText());
////            Element Sessionid = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/sessionid");
////            if (!Sessionid.getText().equals("null")) paymentOrderAcs.setSessionid(Sessionid.getText());
//            Element TermUrl = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/termUrl");
//            if (!TermUrl.getText().equals("null")) paymentOrderAcs.setTermUrl(TermUrl.getText());
//
//            Element SignatureValue = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/responseSignature/signatureValue");
//            String signatureValue = SignatureValue.getText() + "";
//
//            Element SignedString = (Element) xmlDoc.getRootElement().selectSingleNode("//soapenv:Envelope/soapenv:Body/ns:paymentOrderAcsResponse/return/responseSignature/signedString");
//            String signedString = SignedString.getText() + "";
//            logger.info("SignedString: {}", signedString);
//            KKBSign kkbSign = new KKBSign();
//            boolean signatureValid = kkbSign.verify(signedString, signatureValue, keystore, bankAlias, storepass); /// keypass???
//            logger.info("Verify: {}", signatureValid);
//            paymentOrderAcs.setSignatureValid(signatureValid);
//
//            halykPaymentOrderAcsRepository.save(paymentOrderAcs);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return paymentOrderAcs;
//    }

    private HalykPaymentOrderAcs parsePaymentOrderAcsResponse(HalykPaymentOrderAcs paymentOrderAcs, EpayServiceStub.Result response) {
        try {
            paymentOrderAcs.setAcsUrl(response.getAcsUrl());
            paymentOrderAcs.setApprovalcode(response.getApprovalcode());
            paymentOrderAcs.setIntreference(response.getIntreference());
            paymentOrderAcs.setIs3ds(response.getIs3Ds());
            paymentOrderAcs.setMessage(response.getMessage());
            paymentOrderAcs.setOrderid(response.getOrderid());
            paymentOrderAcs.setPareq(response.getPareq());
            paymentOrderAcs.setReference(response.getReference());
            paymentOrderAcs.setReturnCode(response.getReturnCode());
            paymentOrderAcs.setTermUrl(response.getTermUrl());

            String signatureValue = response.getResponseSignature().getSignatureValue();
            String signedString = response.getResponseSignature().getSignedString();
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

//    private String createPaymentOrderAcsXML(HalykPaymentOrderAcs paymentOrderAcs) {
//        String concatString = paymentOrderAcs.getMd() + paymentOrderAcs.getPares() + paymentOrderAcs.getSessionid();
//        KKBSign kkbSign = new KKBSign();
//        String signatureValue = kkbSign.sign64(concatString, keystore, clientAlias, keypass, storepass);
//        String xml = String.format("<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">" +
//                        "<soapenv:Body>" +
//                        "<ns4:paymentOrderAcs xmlns:ns4=\"http://ws.epay.kkb.kz/xsd\">" +
//                        "<order>" +
//                        "<md>%s</md>" +
//                        "<pares>%s</pares>" +
//                        "<sessionid>%s</sessionid>" +
//                        "</order>" +
//                        "<requestSignature>" +
//                        "<merchantCertificate>%s</merchantCertificate>" +
//                        "<merchantId>%s</merchantId>" +
//                        "<signatureValue>%s</signatureValue>" +
//                        "</requestSignature>" +
//                        "</ns4:paymentOrderAcs>" +
//                        "</soapenv:Body>" +
//                        "</soapenv:Envelope>",
//                paymentOrderAcs.getMd(), paymentOrderAcs.getPares(), paymentOrderAcs.getSessionid(),
//                merchantCertificate, merchantid, signatureValue);
//        return xml;
//    }


//    @PostConstruct
//    public void testCreateXML() {
//        try {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
////                        Thread.sleep(1000);
////                        paymentOrder(new BigDecimal("5.00"), "OLEG IVANOFF", "323", "Test payment SOAP",
////                                "12", "0000000074234", "4003035000005378", "25");
//
////                        Thread.sleep(1000);
////                        paymentOrder(new BigDecimal("70.0"), "OLEG IVANOFF", "653", "Test payment SOAP",
////                                "A9", "0000000074244", "440564000006150", "25");
//
////                        paymentOrderAcs("ASDEF8009003","ABCD-AMOUNT5.00-TERMINAL92061102-ORDER0000000074234--OK",
////                                "3B9482631E42175E5B06C568DE0F1132");
//
////                        String status = checkOrder("0000000074244");
////                        logger.info("Status: {}", status);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public String getSessionByMD(String md) {
        HalykOrder paymentOrder = halykOrderRepository.findTopByMd(md);
        return paymentOrder.getSessionid();
    }

    public Cashbox getCashboxByMD(String md) {
        HalykOrder paymentOrder = halykOrderRepository.findTopByMd(md);
        Cashbox cashbox = paymentService.getCashboxByOrderId(paymentOrder.getOrderid());
        return cashbox;
    }

    public Payment getPaymentByMd(String md) {
        HalykOrder paymentOrder = halykOrderRepository.findTopByMd(md);
        Payment payment = paymentService.getPaymentByOrderId(paymentOrder.getOrderid());
        if (Objects.nonNull(payment)) {
            return payment;
        }
        return paymentService.findByPaySysPayId(paymentOrder.getOrderid());
    }

    public String getSessionByPaRes(String paRes) {
        String pareq = paRes.replace("-OK", "");
        HalykOrder paymentOrder = halykOrderRepository.findTopByPareq(pareq);
        return paymentOrder.getSessionid();
    }

    public Payment getPaymentByPaRes(String paRes) {
        String pareq = paRes.replace("-OK", "");
        HalykOrder paymentOrder = halykOrderRepository.findTopByPareq(pareq);
        logger.info("PayOrder: {}", gson.toJson(paymentOrder));
        Payment payment = paymentService.findByPaySysPayId(paymentOrder.getOrderid());
        if (Objects.nonNull(payment)) {
            return payment;
        }
        return paymentService.findByPaySysPayId(paymentOrder.getOrderid());
    }
}
