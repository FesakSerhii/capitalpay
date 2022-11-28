package kz.capitalpay.server.payments.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.dto.PagedResultDto;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.p2p.service.P2pPaymentService;
import kz.capitalpay.server.payments.dto.OnePaymentDetailsRequestDTO;
import kz.capitalpay.server.payments.dto.PaymentFilterDto;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.repository.PaymentRepository;
import kz.capitalpay.server.simple.dto.PaymentDetailDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.Predicate;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.login.service.ApplicationRoleService.*;
import static kz.capitalpay.server.payments.service.PaymentLogService.CHANGE_STATUS_PAYMENT;
import static kz.capitalpay.server.payments.service.PaymentLogService.CREATE_NEW_PAYMENT;
import static kz.capitalpay.server.simple.service.SimpleService.SUCCESS;

@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
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
    @Autowired
    P2pPaymentService p2pPaymentService;


    public boolean checkUnique(Cashbox cashbox, String billid) {
        List<Payment> paymentList = paymentRepository.findByCashboxIdAndBillIdAndStatus(cashbox.getId(), billid, SUCCESS);
        return (paymentList == null || paymentList.size() == 0);
    }

    public ResultDTO newPayment(Payment payment) {
        paymentRepository.save(payment);
        paymentLogService.newEvent(payment.getGuid(), payment.getIpAddress(), CREATE_NEW_PAYMENT, gson.toJson(payment));
        return new ResultDTO(true, payment, 0);
    }

    public Payment getPayment(String paymentId) {
        return paymentRepository.findByGuid(paymentId);
    }

    public void success(Payment paymentFromBd) {
        paymentFromBd.setStatus(SUCCESS);
        paymentRepository.save(paymentFromBd);
    }

    public Payment findById(String paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    public Payment setStatusByPaySysPayId(String paySysPayId, String status) {
        LOGGER.info("PaySysPay ID: {}", paySysPayId);
        Payment payment = paymentRepository.findTopByPaySysPayId(paySysPayId);
        if (payment != null) {
            LOGGER.info("Payment pspid: {}", payment.getPaySysPayId());
            payment.setStatus(status);
            paymentRepository.save(payment);
            paymentLogService.newEvent(payment.getGuid(), payment.getIpAddress(), CHANGE_STATUS_PAYMENT, gson.toJson(payment));
            notifyMerchant(payment);
            LOGGER.info("Change status: {}", gson.toJson(payment));
            return payment;
        } else {
            LOGGER.error("PaySysPay ID: {}", paySysPayId);
            LOGGER.error("Payment: {}", payment);
        }
        return null;
    }

    public void notifyMerchant(Payment payment) {
        LOGGER.info("notifyMerchant()");
        try {
            String interactionUrl = cashboxService.getInteractUrl(payment);
            PaymentDetailDTO detailsJson;
            if (payment.isP2p()) {
                detailsJson = p2pPaymentService.signDetail(payment);
            } else {
                detailsJson = signDetail(payment);
            }
            Map<String, Object> requestJson = new HashMap<>();
            requestJson.put("type", "paymentStatus");
            requestJson.put("data", detailsJson);
            LOGGER.info("unteractionURL {}", interactionUrl);
            String response = restTemplate.postForObject(interactionUrl, requestJson, String.class);
            LOGGER.info(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return paymentRepository.findTopByCashboxIdAndBillId(cashboxid, billid);
    }

    public PaymentDetailDTO signDetail(Payment payment) {
        String secret = cashboxService.getSecret(payment.getCashboxId());
        PaymentDetailDTO paymentDetail = new PaymentDetailDTO();
        paymentDetail.setTimestamp(payment.getTimestamp());
        paymentDetail.setLocalDateTime(payment.getLocalDateTime());
        paymentDetail.setBillId(payment.getBillId());
        paymentDetail.setTotalAmount(payment.getTotalAmount());
        paymentDetail.setCurrency(payment.getCurrency());
        paymentDetail.setDescription(payment.getDescription());
        paymentDetail.setParam(payment.getParam());
        paymentDetail.setStatus(payment.getStatus());
        //    SHA256(cashboxId + billId + status + )
        String unsignedString = payment.getCashboxId().toString() + payment.getBillId() + payment.getStatus() + secret;
        String sha256hex = DigestUtils.sha256Hex(unsignedString);

        LOGGER.info("Unsigned data: {}", unsignedString);
        paymentDetail.setSignature(sha256hex);
        return paymentDetail;
    }

    public ResultDTO paymentList(Principal principal, PaymentFilterDto filter) {
        try {
            ApplicationUser applicationUser = applicationUserService.getUserByLogin(principal.getName());
            if (applicationUser == null) {
                return USER_NOT_FOUND;
            }
            if (applicationUser.getRoles().contains(applicationRoleService.getRole(MERCHANT))) {
                filter.setMerchantId(applicationUser.getId());
            }
            PageRequest request = PageRequest.of(filter.getPage() - 1, filter.getLimit());
            Specification<Payment> specification = buildPaymentSpecification(filter);
            Page<Payment> pagedResult = paymentRepository.findAll(specification, request);
            return new ResultDTO(true, new PagedResultDto<Payment>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), filter.getPage(), pagedResult.getSize(), pagedResult.hasNext(), pagedResult.getContent()), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO paymentList(Principal principal) {
        try {
            ApplicationUser applicationUser = applicationUserService.getUserByLogin(principal.getName());
            if (applicationUser == null) {
                return USER_NOT_FOUND;
            }
            List<Payment> paymentList;
            if (applicationUser.getRoles().contains(applicationRoleService.getRole(OPERATOR))
                    || applicationUser.getRoles().contains(applicationRoleService.getRole(ADMIN))) {
                paymentList = paymentRepository.findAllBySaveBankCardFalse();
            } else {
                paymentList = paymentRepository.findAllByMerchantIdAndSaveBankCardFalse(applicationUser.getId());
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
                return USER_NOT_FOUND;
            }
            Payment payment = paymentRepository.findByGuid(request.getGuid());
            if (payment == null) {
                LOGGER.error("GUID: {}", request.getGuid());
                LOGGER.error("Payment: {}", payment);
                return PAYMENT_NOT_FOUND;
            }
            if (!applicationUser.getRoles().contains(applicationRoleService.getRole(OPERATOR)) && !applicationUser.getRoles().contains(applicationRoleService.getRole(ADMIN))) {
                if (!payment.getMerchantId().equals(applicationUser.getId())) {
                    LOGGER.error("Payment: {}", gson.toJson(payment));
                    LOGGER.error("Merchant: {}", gson.toJson(applicationUser));
                    return NOT_ENOUGH_RIGHTS;
                }
            }
            return new ResultDTO(true, payment, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public Payment generateEmptyPayment(String status) {
        Payment payment = new Payment();
        payment.setSaveBankCard(true);
        payment.setTimestamp(System.currentTimeMillis());
        payment.setGuid(UUID.randomUUID().toString());
        payment.setStatus(status);
//        payment.setPaySysPayId(orderId);
        payment.setPaySysPayId(p2pPaymentService.generateOrderId());
        return paymentRepository.save(payment);
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public ResultDTO getPaymentsMerchantNames(Principal principal) {
        List<Payment> payments;
        ApplicationUser applicationUser = applicationUserService.getUserByLogin(principal.getName());
        if (applicationUser == null) {
            return USER_NOT_FOUND;
        }
        if (applicationUser.getRoles().contains(applicationRoleService.getRole(ADMIN))
                || applicationUser.getRoles().contains(applicationRoleService.getRole(OPERATOR))) {
            payments = paymentRepository.findAllBySaveBankCardFalse();
        } else {
            payments = paymentRepository.findAllByMerchantIdAndSaveBankCardFalse(applicationUser.getId());
        }
        return new ResultDTO(true, payments.stream().map(Payment::getMerchantName).distinct()
                .filter(x -> Objects.nonNull(x) && !x.trim().isEmpty()).sorted().collect(Collectors.toList()), 0);
    }

    private Specification<Payment> buildPaymentSpecification(PaymentFilterDto filter) {
        return (Specification<Payment>) (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.distinct(true);

            LOGGER.info("getAcUsersByFilter()");
            LOGGER.info("after: {}", filter.getDateAfter());
            LOGGER.info("before: {}", filter.getDateBefore());
            LOGGER.info("page: {}", filter.getPage());
            LOGGER.info("limit: {}", filter.getLimit());

            predicates.add(builder.and(builder.equal(root.get("saveBankCard"), false)));
            if (Objects.nonNull(filter.getMerchantId())) {
                predicates.add(builder.and(builder.equal(root.get("merchantId"), filter.getMerchantId())));
            }
            if (Objects.nonNull(filter.getDateAfter()) && Objects.nonNull(filter.getDateBefore())) {
                LocalDateTime after = filter.getDateAfter().atStartOfDay();
                LocalDateTime before = filter.getDateBefore().atTime(23, 59);
                predicates.add(builder.and(builder.greaterThanOrEqualTo(root.get("localDateTime"), after)));
                predicates.add(builder.and(builder.lessThanOrEqualTo(root.get("localDateTime"), before)));
            }
            if (Objects.nonNull(filter.getCurrency()) && !filter.getCurrency().trim().isEmpty()) {
                predicates.add(builder.and(builder.equal(root.get("currency"), filter.getCurrency())));
            }
            if (Objects.nonNull(filter.getMerchantName()) && !filter.getMerchantName().trim().isEmpty()) {
                predicates.add(builder.and(builder.equal(root.get("merchantName"), filter.getMerchantName())));
            }
            if (Objects.nonNull(filter.getBillId()) && !filter.getBillId().trim().isEmpty()) {
                predicates.add(builder.and(builder.equal(root.get("billId"), filter.getBillId())));
            }
            if (Objects.nonNull(filter.getPaymentId()) && !filter.getPaymentId().trim().isEmpty()) {
                predicates.add(builder.and(builder.equal(root.get("guid"), filter.getPaymentId())));
            }
            if (Objects.nonNull(filter.getCashboxName()) && !filter.getCashboxName().trim().isEmpty()) {
                predicates.add(builder.and(builder.equal(root.get("cashboxName"), filter.getCashboxName())));
            }
            if (Objects.nonNull(filter.getBankTerminalId())) {
                predicates.add(builder.and(builder.equal(root.get("bankTerminalId"), filter.getBankTerminalId())));
            }
            if (Objects.nonNull(filter.getTotalAmount())) {
                predicates.add(builder.and(builder.equal(root.get("totalAmount"), filter.getTotalAmount())));
            }

            if (Objects.isNull(filter.getSortDto()) || Objects.isNull(filter.getSortDto().getField()) || filter.getSortDto().getField().trim().isEmpty()) {
                query.orderBy(builder.desc(root.get("localDateTime")));
            } else {
                query.orderBy(filter.getSortDto().isAsc() ? builder.asc(root.get(filter.getSortDto().getField())) : builder.desc(root.get(filter.getSortDto().getField())));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
