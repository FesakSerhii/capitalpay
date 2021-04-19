package kz.capitalpay.server.cashbox.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.CashboxCurrencyEditListDTO;
import kz.capitalpay.server.cashbox.dto.CashboxCurrencyRequestDTO;
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
import java.util.*;

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

    public ResultDTO findAll(Principal principal, CashboxCurrencyRequestDTO request) {
        try {
            Cashbox cashbox = cashboxRepository.findById(request.getCashboxId()).orElse(null);
            if (cashbox == null) {
                return error113;
            }

            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            ApplicationUser operator = null;
            if (!owner.getRoles().contains(applicationRoleService.getRole(OPERATOR)) &&
                    !owner.getRoles().contains(applicationRoleService.getRole(ADMIN)) &&
                    !cashbox.getMerchantId().equals(owner.getId())) {
                return error110;
            }

            String currencyJson = cashboxSettingsService.getField(cashbox.getId(), CASHBOX_CURRENCY_LIST);
            List<String> currencyList = new ArrayList<>();

            if (currencyJson != null && currencyJson.length() > 0) {
                currencyList = gson.fromJson(currencyJson, List.class);
            }
            Set<String> currencySet = new HashSet<>(currencyList);
            logger.info("Set: {}", gson.toJson(currencySet));
            List<SystemCurrency> systemCurrencyList = merchantCurrencyService.currencyList(owner.getId());
            Map<String, Boolean> result = new HashMap<>();
            for (SystemCurrency sk : systemCurrencyList) {
                    logger.info("contains: {}", currencySet.contains(sk.getAlpha()));
                    result.put(sk.getAlpha(), currencySet.contains(sk.getAlpha()));
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
                return error113;
            }

            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());

            if (!cashbox.getMerchantId().equals(owner.getId())) {
                return error110;
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
                    return error112;
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
}
