package kz.capitalpay.server.simple.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxCurrencyService;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.constants.HalykControlOrderCommandTypeDictionary;
import kz.capitalpay.server.currency.service.CurrencyService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.merchantsettings.service.MerchantKycService;
import kz.capitalpay.server.p2p.model.MerchantP2pSettings;
import kz.capitalpay.server.p2p.service.P2pPaymentService;
import kz.capitalpay.server.p2p.service.P2pSettingsService;
import kz.capitalpay.server.paymentlink.service.PaymentLinkService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykBankControlOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykPurchaseOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykBankControlOrderRepository;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykPurchaseOrderRepository;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import kz.capitalpay.server.simple.dto.PaymentDetailDTO;
import kz.capitalpay.server.simple.dto.SimpleRequestDTO;
import kz.capitalpay.server.terminal.model.Terminal;
import kz.capitalpay.server.terminal.repository.TerminalRepository;
import kz.capitalpay.server.usercard.model.UserCardFromBank;
import kz.capitalpay.server.usercard.service.UserCardService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.REDIRECT_FAILED_URL;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.REDIRECT_SUCCESS_URL;

@Service
public class SimpleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleService.class);

    @Autowired
    Gson gson;

    @Autowired
    PaymentService paymentService;

    @Autowired
    CashboxService cashboxService;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CashboxCurrencyService cashboxCurrencyService;

    @Autowired
    MerchantKycService merchantKycService;

    @Autowired
    CashboxSettingsService cashboxSettingsService;

    @Autowired
    HalykSoapService halykSoapService;

    @Autowired
    TerminalRepository terminalRepository;

    @Autowired
    P2pSettingsService p2pSettingsService;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    HalykPurchaseOrderRepository halykPurchaseOrderRepository;
    @Autowired
    HalykBankControlOrderRepository halykBankControlOrderRepository;

    @Autowired
    PaymentLinkService paymentLinkService;
    @Autowired
    UserCardService userCardService;

    @Autowired
    P2pPaymentService p2pPaymentService;

    @Value("${remote.api.addres}")
    String apiAddress;

    @Value("${bank.url}")
    private String bankUrl;

    @Value("${bank.url.test}")
    private String bankTestUrl;

    // Payment Status
    public static final String NEW_PAYMENT = "NEW";
    public static final String SUCCESS = "SUCCESS";
    public static final String SAVE_BANK_CARD = "SAVE_BANK_CARD";
    public static final String COMPLETE_PURCHASE = "COMPLETE_PURCHASE";
    public static final String FAILED = "FAILED";
    public static final String PENDING = "PENDING";

    public static Long lastPaymentId = System.currentTimeMillis() / 10000;


    public ResultDTO newPayment(SimpleRequestDTO request) {
        try {
            if (request.getBillid().length() > 31) {
                return BILL_ID_IS_TOO_LONG;
            }
            Cashbox cashbox = cashboxService.findById(request.getCashboxid());
            if (cashbox == null) {
                return CASHBOX_NOT_FOUND;
            }
            ApplicationUser merchant = applicationUserService.getUserById(cashbox.getMerchantId());
            if (!paymentService.checkUnique(cashbox, request.getBillid())) {
                return BILL_ID_ALREADY_EXISTS;
            }
            String merchantName = merchantKycService.getField(merchant.getId(), MerchantKycService.MNAME);
//            BigDecimal totalAmount = request.getTotalamount()
//                    .movePointLeft(2).setScale(2, RoundingMode.HALF_UP);

            if (!cashboxCurrencyService.checkCurrencyEnable(cashbox.getId(), merchant.getId(), request.getCurrency())) {
                return CURRENCY_NOT_FOUND;
            }
            if (request.getParam() != null && request.getParam().length() > 255) {
                return PARAM_IS_TOO_LONG;
            }
            Payment payment = new Payment();
            payment.setGuid(UUID.randomUUID().toString());
            payment.setTimestamp(System.currentTimeMillis());
            payment.setLocalDateTime(LocalDateTime.now());
            payment.setMerchantId(merchant.getId());
            payment.setMerchantName(merchantName);
            payment.setCashboxId(cashbox.getId());
            payment.setCashboxName(cashbox.getName());
            payment.setBillId(request.getBillid());
            payment.setPaySysPayId(p2pPaymentService.generateOrderId());
            payment.setTotalAmount(request.getTotalamount());
            payment.setCurrency(request.getCurrency());
            payment.setDescription(request.getDescription());
            payment.setParam(request.getParam());
            payment.setIpAddress(request.getIpAddress());
            payment.setUserAgent(request.getUserAgent());
            payment.setStatus(NEW_PAYMENT);
            payment.setPaymentLinkId(request.getPaymentLinkId());
            return paymentService.newPayment(payment);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public ResultDTO createPayment(HttpServletRequest httpRequest, Long cashboxid, String billid,
                                   BigDecimal totalamount, String currency, String description,
                                   String param, String paymentLink) {
        try {
            SimpleRequestDTO request = new SimpleRequestDTO();
            String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = httpRequest.getRemoteAddr();
            }
            request.setIpAddress(ipAddress);
            request.setUserAgent(httpRequest.getHeader("User-Agent"));
            request.setCashboxid(cashboxid);
            request.setBillid(billid);
            request.setTotalamount(totalamount);
            request.setCurrency(currency);
            request.setDescription(description);
            request.setParam(param);
            request.setPaymentLinkId(paymentLink);
            LOGGER.info("Request: {}", gson.toJson(request));
            return newPayment(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO createBankPayment(HttpServletRequest httpRequest, Long cashboxid, String billid,
                                       BigDecimal totalamount, String currency, String description,
                                       String param, String paymentLink) {
        try {
            SimpleRequestDTO request = new SimpleRequestDTO();
            String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = httpRequest.getRemoteAddr();
            }
            request.setIpAddress(ipAddress);
            request.setUserAgent(httpRequest.getHeader("User-Agent"));
            request.setCashboxid(cashboxid);
            request.setBillid(billid);
            request.setTotalamount(totalamount);
            request.setCurrency(currency);
            request.setDescription(description);
            request.setParam(param);
            request.setPaymentLinkId(paymentLink);
            LOGGER.info("Request: {}", gson.toJson(request));
            ResultDTO paymentResult = newPayment(request);
            if (!(paymentResult.getData() instanceof Payment)) {
                return paymentResult;
            }
            Payment payment = (Payment) paymentResult.getData();
            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(payment.getMerchantId());
            if (Objects.isNull(merchantP2pSettings) || Objects.isNull(merchantP2pSettings.getTerminalId())) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                return MERCHANT_TERMINAL_SETTINGS_NOT_FOUND;
            }
            Terminal terminal = terminalRepository.findByIdAndDeletedFalse(merchantP2pSettings.getTerminalId()).orElse(null);
            if (Objects.isNull(terminal)) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                return MERCHANT_TERMINAL_SETTINGS_NOT_FOUND;
            }

            Cashbox cashbox = cashboxService.findById(payment.getCashboxId());
            Map<String, String> resultUrls = cashboxSettingsService.getMerchantResultUrls(cashbox.getId());
            if (terminal.isP2p()) {
                payment.setP2p(true);
                Long merchantCardId = cashboxService.findUserCardIdByCashBoxId(payment.getCashboxId());
                UserCardFromBank merchantCard = userCardService.findUserCardFromBankById(merchantCardId);
                String p2pXml = halykSoapService.createAnonymousP2pXml(payment.getPaySysPayId(), payment.getMerchantId(), merchantCard.getBankCardId(), payment.getTotalAmount(), terminal.getOutputTerminalId());
                LOGGER.info("p2pXml {}", p2pXml);

                String encodedXml = Base64.getEncoder().encodeToString(p2pXml.getBytes());
                Map<String, String> result = new HashMap<>();
                result.put("xml", encodedXml);
                result.put("p2p", "true");
                if (Objects.nonNull(resultUrls)) {
                    result.put("backLink", resultUrls.get(REDIRECT_SUCCESS_URL));
                }
                result.put("postLink", apiAddress + "/api/p2p-link");
                return new ResultDTO(true, result, 0);
            } else {
                String purchaseXml = halykSoapService.createPurchaseXml(payment.getPaySysPayId(), totalamount, terminal.getOutputTerminalId());
                LOGGER.info("purchaseXml {}", purchaseXml);

                String encodedXml = Base64.getEncoder().encodeToString(purchaseXml.getBytes());
                Map<String, String> result = new HashMap<>();
                result.put("xml", encodedXml);
                result.put("p2p", "false");
                result.put("backLink", resultUrls.get(REDIRECT_SUCCESS_URL));
                result.put("FailureBackLink", resultUrls.get(REDIRECT_FAILED_URL));
                result.put("FailurePostLink", apiAddress + "/api/purchase-link");
                result.put("postLink", apiAddress + "/api/purchase-link");
                return new ResultDTO(true, result, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    // Signature: SHA256(cashboxid + billid + secret)
    public ResultDTO getPaymentInfo(HttpServletRequest httpRequest, Long cashboxid, String billid, String signature) {
        try {
            String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = httpRequest.getRemoteAddr();
            }
            LOGGER.info("Order details request from IP: {}", ipAddress);
            LOGGER.info(httpRequest.getHeader("User-Agent"));

            Payment payment = paymentService.getPaymentByBillAndCashbox(billid, cashboxid);
            if (payment == null) {
                LOGGER.error("Payment: {}", payment);
                return new ResultDTO(false, "Payment not found", -1);
            }

            String secret = cashboxService.getSecret(cashboxid);
            String sha256hex = DigestUtils.sha256Hex(cashboxid.toString() + billid + secret);
            if (!sha256hex.equals(signature)) {
                LOGGER.error("Cashbox ID: {}", cashboxid);
                LOGGER.error("Bill ID: {}", billid);
                LOGGER.error("Server sign: {}", sha256hex);
                LOGGER.error("Client sign: {}", signature);
                return new ResultDTO(false, "Signature: SHA256(cashboxid + billid + secret)", -1);
            }

            PaymentDetailDTO paymentDetail = paymentService.signDetail(payment);

            return new ResultDTO(true, paymentDetail, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public void completeBankPurchase(String requestBody) {
        LOGGER.info("completeBankPurchase()");
        if (Objects.isNull(requestBody) || requestBody.trim().isEmpty()) {
            LOGGER.info("requestBody is NULL");
            return;
        }
        String xml = requestBody.replace("response=", "");
        HalykPurchaseOrder halykPurchaseOrder = halykSoapService.parseBankPurchaseOrder(xml);
        if (Objects.isNull(halykPurchaseOrder)) {
            LOGGER.info("halykAnonymousP2pOrder is NULL");
            return;
        }
        halykPurchaseOrderRepository.save(halykPurchaseOrder);
        if (Objects.nonNull(halykPurchaseOrder.getResponseCode()) && halykPurchaseOrder.getResponseCode().equals("00")) {
            Payment mainPayment = paymentService.findByPaySysPayId(halykPurchaseOrder.getOrderId());
            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(mainPayment.getMerchantId());
            if (Objects.isNull(merchantP2pSettings) || Objects.isNull(merchantP2pSettings.getTerminalId())) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                return;
            }
            Terminal terminal = terminalRepository.findByIdAndDeletedFalse(merchantP2pSettings.getTerminalId()).orElse(null);
            if (Objects.isNull(terminal)) {
                LOGGER.info(MERCHANT_TERMINAL_SETTINGS_NOT_FOUND.toString());
                return;
            }
            String controlOrderXml = halykSoapService.createPurchaseControlXml(halykPurchaseOrder.getOrderId(),
                    halykPurchaseOrder.getAmount(),
//                    92061102L,
                    terminal.getOutputTerminalId(),
                    halykPurchaseOrder.getReference(),
                    HalykControlOrderCommandTypeDictionary.COMPLETE);

            String url = bankUrl + "/jsp/remote/control.jsp?" + controlOrderXml;
            LOGGER.info("controlOrder url {}", url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            LOGGER.info("halykBankControlOrder response body {}", response.getBody());
            HalykBankControlOrder halykBankControlOrder = halykSoapService.parseBankControlOrder(response.getBody());
            halykBankControlOrderRepository.save(halykBankControlOrder);
            if (Objects.nonNull(halykBankControlOrder.getResponseCode()) && halykBankControlOrder.getResponseCode().equals("00")) {
                paymentService.setStatusByPaySysPayId(halykPurchaseOrder.getOrderId(), SUCCESS);
                if (Objects.nonNull(mainPayment.getPaymentLinkId())) {
                    paymentLinkService.disablePaymentLink(mainPayment.getPaymentLinkId());
                }
            }
        }
    }
}
