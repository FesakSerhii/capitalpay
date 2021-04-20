package kz.capitalpay.server.currency.service;

import com.google.gson.Gson;
import kz.capitalpay.server.currency.dto.MerchantEditListDTO;
import kz.capitalpay.server.currency.dto.MerchantRequestDTO;
import kz.capitalpay.server.currency.model.SystemCurrency;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.MerchantSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

import static kz.capitalpay.server.constants.ErrorDictionary.error106;
import static kz.capitalpay.server.constants.ErrorDictionary.error112;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.EDIT_MERCHANT_CURRENCY;
import static kz.capitalpay.server.merchantsettings.service.MerchantSettingsService.MERCHANT_CURRENCY_LIST;

@Service
public class MerchantCurrencyService {

    Logger logger = LoggerFactory.getLogger(MerchantCurrencyService.class);

    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    MerchantSettingsService merchantSettingsService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;

    public ResultDTO findAll(MerchantRequestDTO request) {
        try {
            ApplicationUser merchant = applicationUserService.getUserById(request.getMerchantId());
            if (merchant == null) {
                return error106;
            }

            String currencyJson = merchantSettingsService.getField(merchant.getId(), MERCHANT_CURRENCY_LIST);
            logger.info("JSON: {}", currencyJson);
            List<String> currencyList = new ArrayList<>();
            if (currencyJson != null && currencyJson.length() > 0) {
                currencyList = gson.fromJson(currencyJson, List.class);
            }
            logger.info("List: {}", currencyList);
            Set<String> currencySet = new HashSet<>(currencyList);
            logger.info("Set: {}", gson.toJson(currencySet));
            List<SystemCurrency> systemCurrencyList = currencyService.currencyList();
            Map<String, Boolean> result = new HashMap<>();
            for (SystemCurrency sk : systemCurrencyList) {
                if (sk.isEnabled()) {
                    logger.info("contains: {}", currencySet.contains(sk.getAlpha()));

                    result.put(sk.getAlpha(), currencySet.contains(sk.getAlpha()));
                }
            }

            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public List<SystemCurrency> currencyList(Long merchantId) {
        String currencyJson = merchantSettingsService.getField(merchantId, MERCHANT_CURRENCY_LIST);
        logger.info("JSON: {}", currencyJson);
        List<String> currencyList = new ArrayList<>();
        if (currencyJson != null && currencyJson.length() > 0) {
            currencyList = gson.fromJson(currencyJson, List.class);
        }
        logger.info("List: {}", currencyList);
        Set<String> currencySet = new HashSet<>(currencyList);
        logger.info("Set: {}", gson.toJson(currencySet));
        List<SystemCurrency> systemCurrencyList = currencyService.currencyList();
        List<SystemCurrency> result = new ArrayList<>();
        for (SystemCurrency sk : systemCurrencyList) {
            if (sk.isEnabled() && currencySet.contains(sk.getAlpha())) {
                result.add(sk);
            }
        }
        return result;
    }

    public ResultDTO editList(Principal principal, MerchantEditListDTO request) {
        try {
            ApplicationUser merchant = applicationUserService.getUserById(request.getMerchantId());
            if (merchant == null) {
                return error106;
            }

            ApplicationUser operator = applicationUserService.getUserByLogin(principal.getName());

            List<SystemCurrency> systemCurrencyList = currencyService.currencyList();

            for (String s : request.getCurrencyList()) {
                boolean error = true;
                for (SystemCurrency sk : systemCurrencyList) {
                    if (sk.getAlpha().equals(s) && sk.isEnabled()) {
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

            systemEventsLogsService.addNewOperatorAction(operator.getUsername(),
                    EDIT_MERCHANT_CURRENCY, gson.toJson(request), merchant.getId().toString());

            merchantSettingsService.setField(merchant.getId(), MERCHANT_CURRENCY_LIST, currencyJson);

            return new ResultDTO(true, request.getCurrencyList(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }
}
