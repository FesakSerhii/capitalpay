package kz.capitalpay.server.payments.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.CashboxBalanceDTO;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.payments.dto.OnePaymentDetailsRequestDTO;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.repository.PaymentRepository;
import kz.capitalpay.server.simple.dto.PaymentDetailDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;
import static kz.capitalpay.server.login.service.ApplicationRoleService.OPERATOR;
import static kz.capitalpay.server.payments.service.PaymentLogService.CHANGE_STATUS_PAYMENT;
import static kz.capitalpay.server.payments.service.PaymentLogService.CREATE_NEW_PAYMENT;
import static kz.capitalpay.server.simple.service.SimpleService.SUCCESS;

@Service
public class PaymentService {

    Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    Gson gson;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentLogService paymentLogService;

    @Autowired
    CashboxService cashboxService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    ApplicationRoleService applicationRoleService;

    public boolean checkUnic(Cashbox cashbox, String billid) {
        List<Payment> paymentList = paymentRepository.findByCashboxIdAndAndBillId(cashbox.getId(), billid);
        return (paymentList == null || paymentList.size() == 0);
    }

    public ResultDTO newPayment(Payment payment) {

        paymentRepository.save(payment);
        paymentLogService.newEvent(payment.getGuid(), payment.getIpAddress(), CREATE_NEW_PAYMENT,
                gson.toJson(payment));

        return new ResultDTO(true, payment, 0);
    }

    public Payment getPayment(String paymentId) {
        return paymentRepository.findByGuid(paymentId);
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findTopByPaySysPayId(orderId);
    }

    public void success(Payment paymentFromBd) {
        paymentFromBd.setStatus(SUCCESS);
        paymentRepository.save(paymentFromBd);
    }

    public Payment findById(String paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    public Payment setStatusByPaySysPayId(String paySysPayId, String status) {
        logger.info("PaySysPay ID: {}", paySysPayId);
        Payment payment = paymentRepository.findTopByPaySysPayId(paySysPayId);
        if (payment != null) {
            logger.info("Payment pspid: {}", payment.getPaySysPayId());
            payment.setStatus(status);
            paymentRepository.save(payment);
            paymentLogService.newEvent(payment.getGuid(), payment.getIpAddress(), CHANGE_STATUS_PAYMENT,
                    gson.toJson(payment));
            notifyMerchant(payment);
            logger.info("Change status: {}", gson.toJson(payment));
            return payment;
        } else {
            logger.error("PaySysPay ID: {}", paySysPayId);
            logger.error("Payment: {}", payment);
        }
        return null;
    }

    public void notifyMerchant(Payment payment) {
        try {
            String interactionUrl = cashboxService.getInteractUrl(payment);
            PaymentDetailDTO detailsJson = signDetail(payment);
            Map<String, Object> requestJson = new HashMap<>();
            requestJson.put("type", "paymentStatus");
            requestJson.put("data", detailsJson);
            String response = restTemplate.postForObject(interactionUrl,
                    requestJson, String.class, java.util.Optional.ofNullable(null));
            logger.info(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cashbox getCashboxByOrderId(String orderid) {
        Payment payment = paymentRepository.findTopByPaySysPayId(orderid);
        Cashbox cashbox = cashboxService.findById(payment.getCashboxId());
        return cashbox;

    }

    public Payment findByPaySysPayId(String paySysPayId) {
        return paymentRepository.findTopByPaySysPayId(paySysPayId);
    }

    public Payment addPhoneAndEmail(String paymentid, String phone, String email) {
        Payment payment = getPayment(paymentid);
        payment.setPhone(phone);
        payment.setEmail(email);
        paymentRepository.save(payment);
        return payment;
    }

    public Payment getPaymentByBillAndCashbox(String billid, Long cashboxid) {
        return paymentRepository.findTopByCashboxIdAndAndBillId(cashboxid, billid);
    }

//    public PaymentDetailDTO signDetail(Payment payment) {
//        String secret = cashboxService.getSecret(payment.getCashboxId());
//        PaymentDetailDTO paymentDetail = new PaymentDetailDTO();
//        paymentDetail.setTimestamp(payment.getTimestamp());
//        paymentDetail.setLocalDateTime(payment.getLocalDateTime());
//        paymentDetail.setBillId(payment.getBillId());
//        paymentDetail.setTotalAmount(payment.getTotalAmount());
//        paymentDetail.setCurrency(payment.getCurrency());
//        paymentDetail.setDescription(payment.getDescription());
//        paymentDetail.setParam(payment.getParam());
//        paymentDetail.setStatus(payment.getStatus());
//        //    SHA256(cashboxId + billId + status + )
//        String unsignedString = payment.getCashboxId().toString()
//                + payment.getBillId() + payment.getStatus() + secret;
//        String sha256hex = DigestUtils.sha256Hex(unsignedString);
//
//        logger.info("Unsigned data: {}", unsignedString);
//        paymentDetail.setSignature(unsignedString);
//        return paymentDetail;
//    }

    private PaymentDetailDTO signDetail(Payment payment) {
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
        logger.info("Unsigned data: {}", unsignedString);
        paymentDetail.setSignature(unsignedString);
        return paymentDetail;
    }

    public ResultDTO paymentList(Principal principal) {
        try {
            ApplicationUser applicationUser = applicationUserService.getUserByLogin(principal.getName());
            if (applicationUser == null) {
                return error106;
            }
            List<Payment> paymentList = new ArrayList<>();
            if (applicationUser.getRoles().contains(applicationRoleService.getRole(OPERATOR))
                    || applicationUser.getRoles().contains(applicationRoleService.getRole(ADMIN))) {
                paymentList = paymentRepository.findAll();
            } else {
                paymentList = paymentRepository.findByMerchantId(applicationUser.getId());
            }

            paymentList.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

            return new ResultDTO(true, paymentList, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public ResultDTO onePayment(Principal principal, OnePaymentDetailsRequestDTO request) {
        try {
            ApplicationUser applicationUser = applicationUserService.getUserByLogin(principal.getName());
            if (applicationUser == null) {
                return error106;
            }

            Payment payment = paymentRepository.findByGuid(request.getGuid());
            if (payment == null) {
                logger.error("GUID: {}", request.getGuid());
                logger.error("Payment: {}", payment);
                return error118;
            }

            if (!applicationUser.getRoles().contains(applicationRoleService.getRole(OPERATOR))
                    && !applicationUser.getRoles().contains(applicationRoleService.getRole(ADMIN))) {
                if (!payment.getMerchantId().equals(applicationUser.getId())) {
                    logger.error("Payment: {}", gson.toJson(payment));
                    logger.error("Merchant: {}", gson.toJson(applicationUser));
                    return error110;
                }
            }

            return new ResultDTO(true, payment, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public List<CashboxBalanceDTO> getBalance(Long cashboxId) {
        Map<String, BigDecimal> result = new HashMap<>();
        List<Payment> paymentList = paymentRepository.findByCashboxIdAndStatus(cashboxId, SUCCESS);

        for (Payment payment : paymentList) {
            BigDecimal amount = BigDecimal.ZERO;
            if (result.containsKey(payment.getCurrency())) {
                amount = result.get(payment.getCurrency());
            }
            amount = amount.add(payment.getTotalAmount());
            result.put(payment.getCurrency(), amount);
        }
        logger.info("result " + result.toString());

        return result.entrySet().stream()
                .map(o -> new CashboxBalanceDTO(o.getKey(), o.getValue()))
                .collect(Collectors.toList());
    }
}
