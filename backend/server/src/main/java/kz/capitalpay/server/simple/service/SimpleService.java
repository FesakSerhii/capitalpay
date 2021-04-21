package kz.capitalpay.server.simple.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxCurrencyService;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.currency.service.CurrencyService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.paysystems.service.PaysystemService;
import kz.capitalpay.server.simple.dto.SimpleRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import static kz.capitalpay.server.constants.ErrorDictionary.*;

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

    // Payment Status
    public static final String NEW_PAYMENT = "new payment";


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
            BigDecimal totalAmount = BigDecimal.valueOf(request.getTotalamount())
                    .movePointLeft(2).setScale(2, RoundingMode.HALF_UP);

            if (!cashboxCurrencyService.checkCurrencyEnable(cashbox.getId(),merchant.getId(), request.getCurrency())) {
                return error112;
            }

            if(request.getParam().length()>255){
                return error117;
            }

            Payment payment = new Payment();
            payment.setGuid(UUID.randomUUID().toString());
            payment.setTimestamp(System.currentTimeMillis());
            payment.setLocalDateTime(LocalDateTime.now());
            payment.setMerchantId(merchant.getId());
            payment.setCashboxId(cashbox.getId());
            payment.setBillId(request.getBillid());
            payment.setTotalAmount(totalAmount);
            payment.setCurrency(request.getCurrency());
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
}
