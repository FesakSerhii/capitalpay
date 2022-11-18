package kz.capitalpay.server.p2p.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.repository.PaymentRepository;
import kz.capitalpay.server.payments.service.PaymentLogService;
import kz.capitalpay.server.simple.dto.PaymentDetailDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static kz.capitalpay.server.payments.service.PaymentLogService.CHANGE_STATUS_PAYMENT;
import static kz.capitalpay.server.simple.service.SimpleService.NEW_PAYMENT;

@Service
public class P2pPaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(P2pPaymentService.class);
    private final PaymentRepository paymentRepository;
    private final PaymentLogService paymentLogService;
    private final Gson gson;
    private final CashboxService cashboxService;
    private final RestTemplate restTemplate;

    public P2pPaymentService(PaymentRepository paymentRepository, PaymentLogService paymentLogService, Gson gson, CashboxService cashboxService, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.paymentLogService = paymentLogService;
        this.gson = gson;
        this.cashboxService = cashboxService;
        this.restTemplate = restTemplate;
    }

    public Payment generateP2pPayment(String ipAddress, String userAgent, Long merchantId, BigDecimal totalAmount, Long cashBoxId, boolean toClient, String currency, String param) {
        Payment payment = new Payment();
        payment.setPaySysPayId(generateOrderId());
        payment.setGuid(UUID.randomUUID().toString());
        payment.setCurrency(currency);
        payment.setLocalDateTime(LocalDateTime.now());
        payment.setIpAddress(ipAddress);
        payment.setCashboxId(cashBoxId);
        payment.setMerchantId(merchantId);
        payment.setTimestamp(System.currentTimeMillis());
        payment.setTotalAmount(totalAmount);
        payment.setUserAgent(userAgent);
        payment.setOutgoing(toClient);
        payment.setParam(param);
        payment.setP2p(true);
        payment.setStatus(NEW_PAYMENT);
        payment = paymentRepository.save(payment);
        paymentLogService.newEvent(payment.getGuid(), ipAddress, NEW_PAYMENT, gson.toJson(payment));
        return payment;
    }

    public String generateOrderId() {
        Payment lastPayment = paymentRepository.findLastByPaySysPayId().orElse(null);
        if (Objects.isNull(lastPayment)) {
            return "00000173801600";
        }
        long lastOrderIdLong = Long.parseLong(lastPayment.getPaySysPayId());
        Long newOrderId = lastOrderIdLong + 1;
        StringBuilder zerosStr = new StringBuilder();
        if (String.valueOf(newOrderId).length() < 14) {
            zerosStr.append("0".repeat(Math.max(0, 14 - String.valueOf(newOrderId).length())));
        }
        return zerosStr.toString() + newOrderId;
    }

    public Payment findById(String paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    public Payment findByOrderId(String orderId) {
        return paymentRepository.findByPaySysPayId(orderId).orElse(null);
    }


//    public P2pPayment addPhoneAndEmail(String paymentId, String phone, String email) {
//        P2pPayment payment = findById(paymentId);
//        if (Objects.isNull(payment)) {
//            return null;
//        }
//        payment.setPhone(phone);
//        payment.setEmail(email);
//        p2pPaymentRepository.save(payment);
//        return payment;
//    }

    public Payment setStatusByPaySysPayId(String orderId, String status) {
        LOGGER.info("PaySysPay ID: {}", orderId);
        Payment payment = paymentRepository.findByPaySysPayId(orderId).orElse(null);
        if (payment != null) {
            LOGGER.info("Payment pspid: {}", payment.getPaySysPayId());
            payment.setStatus(status);
            paymentRepository.save(payment);
            paymentLogService.newEvent(payment.getGuid(), payment.getIpAddress(), CHANGE_STATUS_PAYMENT, gson.toJson(payment));
            notifyMerchant(payment);
            LOGGER.info("Change status: {}", gson.toJson(payment));
            return payment;
        } else {
            LOGGER.error("PaySysPay ID: {}", orderId);
            LOGGER.error("Payment: {}", payment);
        }
        return null;
    }

    public ResultDTO getP2pPaymentDataById(String id) {
        Payment p2pPayment = findById(id);
        if (Objects.isNull(p2pPayment)) {
            return ErrorDictionary.PAYMENT_NOT_FOUND;
        }
        return new ResultDTO(true, p2pPayment, 0);
    }

    private void notifyMerchant(Payment payment) {
        try {
            String interactionUrl = cashboxService.getInteractUrl(payment.getCashboxId());
            PaymentDetailDTO detailsJson = signDetail(payment);
            Map<String, Object> requestJson = new HashMap<>();
            requestJson.put("type", "paymentStatus");
            requestJson.put("data", detailsJson);
            String response = restTemplate.postForObject(interactionUrl, requestJson, String.class, java.util.Optional.ofNullable(null));
            LOGGER.info(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PaymentDetailDTO signDetail(Payment payment) {
        String secret = cashboxService.getSecret(payment.getCashboxId());
        PaymentDetailDTO paymentDetail = new PaymentDetailDTO();
        paymentDetail.setTimestamp(payment.getTimestamp());
        paymentDetail.setLocalDateTime(payment.getLocalDateTime());
        paymentDetail.setTotalAmount(payment.getTotalAmount());
        paymentDetail.setCurrency(payment.getCurrency());
        paymentDetail.setParam(payment.getParam());
        paymentDetail.setStatus(payment.getStatus());
        BigDecimal amount = payment.getTotalAmount().setScale(2, RoundingMode.HALF_UP);
        String unsignedString = payment.getCashboxId().toString() + payment.getStatus() + amount.toString() + secret;
        String sha256hex = DigestUtils.sha256Hex(unsignedString);
        LOGGER.info("Unsigned data: {}", unsignedString);
        paymentDetail.setSignature(sha256hex);
        return paymentDetail;
    }
}
