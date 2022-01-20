package kz.capitalpay.server.p2p.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.model.P2pPayment;
import kz.capitalpay.server.p2p.repository.P2pPaymentRepository;
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

    private final P2pPaymentRepository p2pPaymentRepository;
    private final P2pPaymentLogService p2pPaymentLogService;
    private final Gson gson;
    private final CashboxService cashboxService;
    private final RestTemplate restTemplate;

    public P2pPaymentService(P2pPaymentRepository p2pPaymentRepository, P2pPaymentLogService p2pPaymentLogService, Gson gson, CashboxService cashboxService, RestTemplate restTemplate) {
        this.p2pPaymentRepository = p2pPaymentRepository;
        this.p2pPaymentLogService = p2pPaymentLogService;
        this.gson = gson;
        this.cashboxService = cashboxService;
        this.restTemplate = restTemplate;
    }

    public P2pPayment generateP2pPayment(String ipAddress, String userAgent, Long merchantId, BigDecimal totalAmount,
                                         Long cashBoxId, boolean toClient, String currency, String param) {
        P2pPayment payment = new P2pPayment();
        P2pPayment lastPayment = p2pPaymentRepository.findLast().orElse(null);
        if (Objects.nonNull(lastPayment)) {
            payment.setOrderId(generateOrderId(lastPayment.getOrderId()));
        } else {
            payment.setOrderId("00000173801600");
        }
        payment.setGuid(UUID.randomUUID().toString());
        payment.setCurrency(currency);
        payment.setLocalDateTime(LocalDateTime.now());
        payment.setIpAddress(ipAddress);
        payment.setCashboxId(cashBoxId);
        payment.setMerchantId(merchantId);
        payment.setTimestamp(System.currentTimeMillis());
        payment.setTotalAmount(totalAmount);
        payment.setUserAgent(userAgent);
        payment.setToClient(toClient);
        payment.setParam(param);
        payment.setStatus(NEW_PAYMENT);
        payment = p2pPaymentRepository.save(payment);
        p2pPaymentLogService.newEvent(payment.getGuid(), ipAddress, NEW_PAYMENT, gson.toJson(payment));
        return payment;
    }

    public String generateOrderId(String lastOrderId) {
        Long lastOrderIdLong = Long.parseLong(lastOrderId);
        Long newOrderId = lastOrderIdLong + 1;
        StringBuilder zerosStr = new StringBuilder();
        if (String.valueOf(newOrderId).length() < 14) {
            zerosStr.append("0".repeat(Math.max(0, 14 - String.valueOf(newOrderId).length())));
        }
        return zerosStr.toString() + newOrderId;
    }

    public P2pPayment findById(String paymentId) {
        return p2pPaymentRepository.findById(paymentId).orElse(null);
    }

    public P2pPayment findByOrderId(String orderId) {
        return p2pPaymentRepository.findByOrderId(orderId).orElse(null);
    }


    public P2pPayment addPhoneAndEmail(String paymentId, String phone, String email) {
        P2pPayment payment = findById(paymentId);
        if (Objects.isNull(payment)) {
            return null;
        }
        payment.setPhone(phone);
        payment.setEmail(email);
        p2pPaymentRepository.save(payment);
        return payment;
    }

    public P2pPayment setStatusByPaySysPayId(String paySysPayId, String status) {
        LOGGER.info("PaySysPay ID: {}", paySysPayId);
        P2pPayment payment = p2pPaymentRepository.findByOrderId(paySysPayId).orElse(null);
        if (payment != null) {
            LOGGER.info("Payment pspid: {}", payment.getOrderId());
            payment.setStatus(status);
            p2pPaymentRepository.save(payment);
            p2pPaymentLogService.newEvent(payment.getGuid(), payment.getIpAddress(), CHANGE_STATUS_PAYMENT,
                    gson.toJson(payment));
            notifyMerchant(payment);
            LOGGER.info("Change status: {}", gson.toJson(payment));
            return payment;
        } else {
            LOGGER.error("PaySysPay ID: {}", paySysPayId);
            LOGGER.error("Payment: {}", payment);
        }
        return null;
    }

    public ResultDTO getP2pPaymentDataById(String id) {
        P2pPayment p2pPayment = findById(id);
        if (Objects.isNull(p2pPayment)) {
            return ErrorDictionary.error118;
        }
        return new ResultDTO(true, p2pPayment, 0);
    }

    private void notifyMerchant(P2pPayment payment) {
        try {
            String interactionUrl = cashboxService.getInteractUrl(payment.getCashboxId());
            PaymentDetailDTO detailsJson = signDetail(payment);
            Map<String, Object> requestJson = new HashMap<>();
            requestJson.put("type", "paymentStatus");
            requestJson.put("data", detailsJson);
            String response = restTemplate.postForObject(interactionUrl,
                    requestJson, String.class, java.util.Optional.ofNullable(null));
            LOGGER.info(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PaymentDetailDTO signDetail(P2pPayment payment) {
        String secret = cashboxService.getSecret(payment.getCashboxId());
        PaymentDetailDTO paymentDetail = new PaymentDetailDTO();
        paymentDetail.setTimestamp(payment.getTimestamp());
        paymentDetail.setLocalDateTime(payment.getLocalDateTime());
        paymentDetail.setTotalAmount(payment.getTotalAmount());
        paymentDetail.setCurrency(payment.getCurrency());
        paymentDetail.setParam(payment.getParam());
        paymentDetail.setStatus(payment.getStatus());
        BigDecimal amount = payment.getTotalAmount().movePointLeft(2).setScale(2, RoundingMode.HALF_UP);
        String sha256hex = DigestUtils.sha256Hex(payment.getCashboxId().toString() + payment.getStatus() + amount.toString() + secret);
        paymentDetail.setSignature(sha256hex);
        return paymentDetail;
    }
}
