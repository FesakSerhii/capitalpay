package kz.capitalpay.server.merchantsettings.service;

import com.google.gson.Gson;
import kz.capitalpay.server.merchantsettings.model.MerchantSettings;
import kz.capitalpay.server.merchantsettings.repository.MerchantSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantSettingsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantSettingsService.class);

    @Autowired
    private Gson gson;

    @Autowired
    private MerchantSettingsRepository merchantSettingsRepository;

    public static final String MERCHANT_CURRENCY_LIST = "currencylist";
    public static final String MERCHANT_PAYSYSTEM_LIST = "paysystemlist";

    public String getField(Long id, String fieldName) {
        MerchantSettings merchantSettings = merchantSettingsRepository.findTopByFieldNameAndMerchantId(fieldName, id);
        if (merchantSettings == null) {
            merchantSettings = new MerchantSettings();
            merchantSettings.setMerchantId(id);
            merchantSettings.setFieldName(fieldName);
            merchantSettings.setFieldValue("");
            merchantSettingsRepository.save(merchantSettings);
        }
        return merchantSettings.getFieldValue();
    }


    public void setField(Long merchantId, String fieldName, String fieldValue) {
        MerchantSettings merchantSettings = merchantSettingsRepository.findTopByFieldNameAndMerchantId(fieldName, merchantId);
        if (merchantSettings == null) {
            merchantSettings = new MerchantSettings();
            merchantSettings.setFieldName(fieldName);
            merchantSettings.setMerchantId(merchantId);
        }
        merchantSettings.setFieldValue(fieldValue);
        merchantSettingsRepository.save(merchantSettings);
    }
}
