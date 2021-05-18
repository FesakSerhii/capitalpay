package kz.capitalpay.server.cashbox.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.CashboxCreateRequestDTO;
import kz.capitalpay.server.cashbox.dto.CashboxNameRequestDTO;
import kz.capitalpay.server.cashbox.dto.CashboxRequestDTO;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.currency.dto.MerchantRequestDTO;
import kz.capitalpay.server.currency.service.CurrencyService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.payments.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.List;
import java.util.Random;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.*;
import static kz.capitalpay.server.simple.service.SimpleService.FAILED;
import static kz.capitalpay.server.simple.service.SimpleService.SUCCESS;

@Service
public class CashboxService {

    Logger logger = LoggerFactory.getLogger(CashboxService.class);

    Random random = new Random();

    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CashboxSettingsService cashboxSettingsService;

    @Autowired
    CashboxRepository cashboxRepository;

    @Autowired
    ApplicationRoleService applicationRoleService;

    public ResultDTO createNew(Principal principal, CashboxCreateRequestDTO request) {
        try {
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            Cashbox cashbox = new Cashbox();
            cashbox.setMerchantId(owner.getId());
            cashbox.setName(request.getName());

            cashboxRepository.save(cashbox);

            if (currencyService.checkEnable(request.getCurrency())) {
                cashboxSettingsService.setField(cashbox.getId(), CASHBOX_CURRENCY_LIST, gson.toJson(List.of(request.getCurrency())));
            } else {
                cashboxRepository.delete(cashbox);
                return error112;
            }
            return new ResultDTO(true, cashbox, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO changeName(Principal principal, CashboxNameRequestDTO request) {
        try {
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            Cashbox cashbox = cashboxRepository.findById(request.getCashboxId()).orElse(null);
            if (cashbox == null) {
                return error113;
            }
            if (!cashbox.getMerchantId().equals(owner.getId())) {
                return error110;
            }
            cashbox.setName(request.getName());

            cashboxRepository.save(cashbox);

            return new ResultDTO(true, cashbox, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO delete(Principal principal, CashboxRequestDTO request) {
        try {
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            Cashbox cashbox = cashboxRepository.findById(request.getCashboxId()).orElse(null);
            if (cashbox == null) {
                return error113;
            }
            if (!cashbox.getMerchantId().equals(owner.getId())) {
                return error110;
            }
            cashbox.setDeleted(true);

            cashboxRepository.save(cashbox);
            List<Cashbox> cashboxList = cashboxRepository.findByMerchantIdAndDeleted(owner.getId(), false);

            return new ResultDTO(true, cashboxList, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO list(Principal principal, MerchantRequestDTO request) {

        try {
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());

            List<Cashbox> cashboxList = cashboxRepository.findByMerchantIdAndDeleted(owner.getId(), false);

            return new ResultDTO(true, cashboxList, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public Cashbox findById(Long cashboxid) {
        return cashboxRepository.findById(cashboxid).orElse(null);
    }


    public String getUrlByPayment(Payment payment) {
        try {
            if (payment.getStatus().equals(SUCCESS)) {
                return cashboxSettingsService.getField(payment.getCashboxId(), REDIRECT_SUCCESS_URL);
            } else if (payment.getStatus().equals(FAILED)) {
                return cashboxSettingsService.getField(payment.getCashboxId(), REDIRECT_FAILED_URL);
            } else {
                return cashboxSettingsService.getField(payment.getCashboxId(), REDIRECT_PENDING_URL);
            }
        } catch (Exception e) {
            logger.error(payment.getCashboxId().toString());
            e.printStackTrace();
        }
        return null;
    }

    public String getSecret(Long cashboxid) {
        String secret = cashboxSettingsService.getField(cashboxid, SECRET);
        if (secret.length() == 0) {
            StringBuilder sb = new StringBuilder();
            String dictionary = "0123456789QWERTYUPASDFGHJKLZXCVBNMqwertyuipasdfghjkzxcvnm";
            for (int i = 0; i < 16; i++) {
                char c = dictionary.charAt(random.nextInt(dictionary.length()));
                sb.append(c);
            }
            secret = sb.toString();
            cashboxSettingsService.setField(cashboxid, SECRET, secret);
        }
        return secret;
    }

    public String getRedirectForPayment(Payment payment) {

        Cashbox cashbox = cashboxRepository.findById(payment.getCashboxId()).orElse(null);
        if (cashbox == null) {
            logger.error("Cashbox: {}", cashbox);
            return "error";
        }
        String location = cashboxSettingsService.getField(payment.getCashboxId(), REDIRECT_URL)
                + "?billid=" + payment.getBillId()
                + "&status=" + payment.getStatus()
                + "&paymentid=" + payment.getGuid();
        if(payment.getParam()!=null && payment.getParam().length()>0){
            location = location + "&param="+payment.getParam();
        }

        return URLEncoder.encode( location, Charset.defaultCharset());
    }
}
