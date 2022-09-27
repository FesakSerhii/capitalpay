package kz.capitalpay.server.cashbox.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.CashboxCurrencyEditListDTO;
import kz.capitalpay.server.cashbox.dto.CashboxRequestDTO;
import kz.capitalpay.server.cashbox.dto.CurrencyDTO;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.currency.model.SystemCurrency;
import kz.capitalpay.server.currency.service.CurrencyService;
import kz.capitalpay.server.currency.service.MerchantCurrencyService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;
import static kz.capitalpay.server.login.service.ApplicationRoleService.OPERATOR;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.CASHBOX_CURRENCY_LIST;

@Service
public class CashboxCurrencyService {

    Logger logger = LoggerFactory.getLogger(CashboxCurrencyService.class);

    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    ApplicationRoleService applicationRoleService;

    @Autowired
    CashboxRepository cashboxRepository;

    @Autowired
    CashboxSettingsService cashboxSettingsService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;

    @Autowired
    MerchantCurrencyService merchantCurrencyService;

    public ResultDTO findAll(Principal principal, CashboxRequestDTO request) {
        try {
            Cashbox cashbox = cashboxRepository.findById(request.getCashboxId()).orElse(null);
            if (cashbox == null) {
                return CASHBOX_NOT_FOUND;
            }

            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            if (!owner.getRoles().contains(applicationRoleService.getRole(OPERATOR)) &&
                    !owner.getRoles().contains(applicationRoleService.getRole(ADMIN)) &&
                    !cashbox.getMerchantId().equals(owner.getId())) {
                return NOT_ENOUGH_RIGHTS;
            }

            String currencyJson = cashboxSettingsService.getField(cashbox.getId(), CASHBOX_CURRENCY_LIST);
            List<String> currencyList = new ArrayList<>();

            if (currencyJson != null && currencyJson.length() > 0) {
                currencyList = gson.fromJson(currencyJson, List.class);
            }
            Set<String> currencySet = new HashSet<>(currencyList);
            logger.info("Set: {}", gson.toJson(currencySet));
            List<SystemCurrency> systemCurrencyList = merchantCurrencyService.currencyList(owner.getId());
            List<CurrencyDTO> result = new ArrayList<>();
            for (SystemCurrency sk : systemCurrencyList) {
                logger.info("contains: {}", currencySet.contains(sk.getAlpha()));
                result.add(new CurrencyDTO(sk.getAlpha(), sk.getNumber(), sk.getUnicode(), sk.getName(), sk.isEnabled()));
            }
            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    public ResultDTO editList(Principal principal, CashboxCurrencyEditListDTO request) {
        try {
            Cashbox cashbox = cashboxRepository.findById(request.getCashboxId()).orElse(null);
            if (cashbox == null) {
                return CASHBOX_NOT_FOUND;
            }

            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            if (!cashbox.getMerchantId().equals(owner.getId())) {
                return NOT_ENOUGH_RIGHTS;
            }

            List<SystemCurrency> systemCurrencyList = merchantCurrencyService.currencyList(owner.getId());
            for (String s : request.getCurrencyList()) {
                boolean error = true;
                for (SystemCurrency sk : systemCurrencyList) {
                    if (sk.getAlpha().equals(s)) {
                        error = false;
                        break;
                    }
                }
                if (error) {
                    return CURRENCY_NOT_FOUND;
                }
            }
            String currencyJson = gson.toJson(request.getCurrencyList());
            logger.info(currencyJson);
            cashboxSettingsService.setField(cashbox.getId(), CASHBOX_CURRENCY_LIST, currencyJson);
            return new ResultDTO(true, request.getCurrencyList(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public boolean checkCurrencyEnable(Long cashboxId, Long merchantId, String currency) {
        String currencyJson = cashboxSettingsService.getField(cashboxId, CASHBOX_CURRENCY_LIST);
        List<String> currencyList = new ArrayList<>();
        if (currencyJson != null && currencyJson.length() > 0) {
            currencyList = gson.fromJson(currencyJson, List.class);
        }
        return currencyList.contains(currency) &&
                merchantCurrencyService.checkCurrencyEnable(merchantId, currency);
    }
}
