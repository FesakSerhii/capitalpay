package kz.capitalpay.server.merchantsettings.service;

import com.google.gson.Gson;
import kz.capitalpay.server.merchantsettings.model.CashboxSettings;
import kz.capitalpay.server.merchantsettings.repository.CashboxSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CashboxSettingsService {

    Logger logger = LoggerFactory.getLogger(CashboxSettingsService.class);

    @Autowired
    Gson gson;

    @Autowired
    CashboxSettingsRepository cashboxSettingsRepository;

    public static final String CASHBOX_CURRENCY_LIST = "currencylist";
    public static final String CASHBOX_PAYSYSTEM_LIST = "paysystemlist";
    public static final String INTERACTION_URL = "interactionurl";
    public static final String REDIRECT_URL = "redirecturl";
    public static final String REDIRECT_SUCCESS_URL = "redirectsuccess";
    public static final String REDIRECT_FAILED_URL = "redirectfailed";
    public static final String REDIRECT_PENDING_URL = "redirectpending";
    public static final String SECRET = "secret";
    public static final String PERCENT_PAYMENT_SYSTEM = "percent_payment_system";

    public String getField(Long cashboxId, String fieldName) {
        CashboxSettings cashboxSettings = cashboxSettingsRepository.findTopByFieldNameAndCashboxId(fieldName, cashboxId);
        if (cashboxSettings == null) {
            cashboxSettings = new CashboxSettings();
            cashboxSettings.setCashboxId(cashboxId);
            cashboxSettings.setFieldName(fieldName);
            cashboxSettings.setFieldValue("");
            cashboxSettingsRepository.save(cashboxSettings);
        }
        return cashboxSettings.getFieldValue();
    }



    public void setField(Long cashboxId, String fieldName, String fieldValue) {
        CashboxSettings cashboxSettings = cashboxSettingsRepository.
                findTopByFieldNameAndCashboxId(fieldName, cashboxId);
        if (cashboxSettings == null) {
            cashboxSettings = new CashboxSettings();
            cashboxSettings.setFieldName(fieldName);
            cashboxSettings.setCashboxId(cashboxId);
        }
        cashboxSettings.setFieldValue(fieldValue);
        cashboxSettingsRepository.save(cashboxSettings);
    }
}
