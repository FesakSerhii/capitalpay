package kz.capitalpay.server.simple.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxCurrencyService;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.currency.service.CurrencyService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.merchantsettings.service.MerchantKycService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.simple.dto.PaymentDetailDTO;
import kz.capitalpay.server.simple.dto.SimpleRequestDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.CLIENT_FEE;
import static kz.capitalpay.server.merchantsettings.service.MerchantKycService.TOTAL_FEE;

@Service
public class SimpleService {

    Logger logger = LoggerFactory.getLogger(SimpleService.class);

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

    // Payment Status
    public static final String NEW_PAYMENT = "NEW";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILED";
    public static final String PENDING = "PENDING";

    public static Long lastPaymentId = System.currentTimeMillis() / 10000;


    public ResultDTO newPayment(SimpleRequestDTO request) {
        try {
            if (request.getBillid().length() > 31) {
                return error115;
            }

            Cashbox cashbox = cashboxService.findById(request.getCashboxid());
            if (cashbox == null) {
                return error113;
            }
            ApplicationUser merchant = applicationUserService.getUserById(cashbox.getMerchantId());
            if (!paymentService.checkUnic(cashbox, request.getBillid())) {
                return error116;
            }

            String merchantName = merchantKycService.getField(merchant.getId(), MerchantKycService.MNAME);

            BigDecimal totalAmount = request.getTotalamount()
                    .movePointLeft(2).setScale(2, RoundingMode.HALF_UP);

            if (!cashboxCurrencyService.checkCurrencyEnable(cashbox.getId(), merchant.getId(), request.getCurrency())) {
                return error112;
            }

            if (request.getParam() != null && request.getParam().length() > 255) {
                return error117;
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
            payment.setPaySysPayId(String.format("%1$14s", lastPaymentId++)
                    .replace(' ', '0'));
            payment.setTotalAmount(totalAmount);
            payment.setCurrency(request.getCurrency());
            payment.setDescription(request.getDescription());
            payment.setParam(request.getParam());
            payment.setIpAddress(request.getIpAddress());
            payment.setUserAgent(request.getUserAgent());
            payment.setStatus(NEW_PAYMENT);

            return paymentService.newPayment(payment);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public ResultDTO createPayment(HttpServletRequest httpRequest, Long cashboxid, String billid, BigDecimal totalamount, String currency, String description, String param) {
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
            request.setTotalamount(adjustmentPriceForClientByClientFee(cashboxid, totalamount));
            request.setCurrency(currency);
            request.setDescription(description);
            request.setParam(param);

            logger.info("Request: {}", gson.toJson(request));

            return newPayment(request);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private BigDecimal adjustmentPriceForClientByClientFee(Long cashboxId, BigDecimal totalAmount) {
        BigDecimal oneHundred = new BigDecimal(100);
        Cashbox cashbox = cashboxService.findById(cashboxId);
        BigDecimal totalFee = BigDecimal.valueOf(Long.parseLong(merchantKycService.getField(cashbox.getMerchantId(),
                TOTAL_FEE)));
        BigDecimal clientFee = BigDecimal.valueOf(Long.parseLong(cashboxSettingsService
                .getField(cashboxId, CLIENT_FEE)));

        BigDecimal percentMerchantWantPaySystem = totalFee.subtract(clientFee);
        BigDecimal totalAmountThatMerchantWantReceived = totalAmount
                .subtract(totalAmount
                        .multiply(percentMerchantWantPaySystem
                                .divide(oneHundred)));
        BigDecimal finalPriceCustomerAfterAdjustmentInPenny = totalAmountThatMerchantWantReceived
                .divide(new BigDecimal("1")
                        .subtract(totalFee
                                .divide(oneHundred, MathContext.DECIMAL128)), MathContext.DECIMAL128)
                .multiply(oneHundred);

        return finalPriceCustomerAfterAdjustmentInPenny.setScale(0, RoundingMode.HALF_UP);
    }

    // Signature: SHA256(cashboxid + billid + secret)
    public ResultDTO getPaymentInfo(HttpServletRequest httpRequest, Long cashboxid, String billid, String signature) {
        try {
            String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = httpRequest.getRemoteAddr();
            }
            logger.info("Order details request from IP: {}", ipAddress);
            logger.info(httpRequest.getHeader("User-Agent"));

            Payment payment = paymentService.getPaymentByBillAndCashbox(billid, cashboxid);
            if (payment == null) {
                logger.error("Payment: {}", payment);
                return new ResultDTO(false, "Payment not found", -1);
            }

            String secret = cashboxService.getSecret(cashboxid);
            String sha256hex = DigestUtils.sha256Hex(cashboxid.toString() + billid + secret);
            if (!sha256hex.equals(signature)) {
                logger.error("Cashbox ID: {}", cashboxid);
                logger.error("Bill ID: {}", billid);
                logger.error("Server sign: {}", sha256hex);
                logger.error("Client sign: {}", signature);
                return new ResultDTO(false, "Signature: SHA256(cashboxid + billid + secret)", -1);
            }

            PaymentDetailDTO paymentDetail = paymentService.signDetail(payment);

            return new ResultDTO(true, paymentDetail, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
