package kz.capitalpay.server.currency.service;

import com.google.gson.Gson;
import kz.capitalpay.server.currency.dto.MerchantRequestDTO;
import kz.capitalpay.server.currency.model.SystemCurrency;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.model.MerchantSettings;
import kz.capitalpay.server.merchantsettings.service.MerchantSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static kz.capitalpay.server.constants.ErrorDictionary.error106;
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

    public ResultDTO findAll(MerchantRequestDTO request) {
        try {
            ApplicationUser merchant = applicationUserService.getUserById(request.getMerchantId());
            if (merchant == null) {
                return error106;
            }

            String currencyJson = merchantSettingsService.getField(merchant.getId(), MERCHANT_CURRENCY_LIST);

            List<String> currencyList = new ArrayList<>();
            if (currencyJson != null && currencyJson.length() > 0) {
                gson.fromJson(currencyJson, List.class);
            }

            Set<String> currencySet = new HashSet<>(currencyList);

            List<SystemCurrency> systemCurrencyList = currencyService.currencyList();
            Map<String, Boolean> result = new HashMap<>();
            for (SystemCurrency sk : systemCurrencyList) {
                if (sk.isEnabled()) {
                    result.put(sk.getAlpha(), currencySet.contains(sk.getAlpha()));
                }
            }

            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }
}
