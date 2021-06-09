package kz.capitalpay.server.merchantsettings.service;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.dto.CashBoxSettingDTO;
import kz.capitalpay.server.merchantsettings.dto.CashBoxSettingFieldDTO;
import kz.capitalpay.server.merchantsettings.model.CashboxSettings;
import kz.capitalpay.server.merchantsettings.repository.CashboxSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import static kz.capitalpay.server.constants.ErrorDictionary.error121;
import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@Service
public class CashboxSettingsService {

    @Autowired
    private CashboxSettingsRepository cashboxSettingsRepository;

    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    private ApplicationRoleService applicationRoleService;

    public static final String CASHBOX_CURRENCY_LIST = "currencylist";
    public static final String CASHBOX_PAYSYSTEM_LIST = "paysystemlist";
    public static final String INTERACTION_URL = "interactionurl";
    public static final String REDIRECT_URL = "redirecturl";
    public static final String REDIRECT_SUCCESS_URL = "redirectsuccess";
    public static final String REDIRECT_FAILED_URL = "redirectfailed";
    public static final String REDIRECT_PENDING_URL = "redirectpending";
    public static final String SECRET = "secret";
    public static final String PERCENT_PAYMENT_SYSTEM = "percent_payment_system";

    public ResultDTO setOrUpdateCashboxSettings(Principal principal, CashBoxSettingDTO request) {
        try {
            ApplicationUser admin = applicationUserService.getUserByLogin(principal.getName());
            if (admin.getRoles().contains(applicationRoleService.getRole(ADMIN))
                    || admin.getRoles().contains(applicationRoleService.getRole(OPERATOR))
                    || admin.getRoles().contains(applicationRoleService.getRole(MERCHANT))) {
                for (CashBoxSettingFieldDTO field : request.getFields()) {
                    setField(field.getCashBoxId(), field.getFieldName(), field.getFieldValue());
                }
                return new ResultDTO(true, request.getFields(), 0);
            } else {
                return error121;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO getCashboxSettings(Principal principal, CashBoxSettingDTO cashBoxDTO) {
        try {
            ApplicationUser admin = applicationUserService.getUserByLogin(principal.getName());
            if (admin.getRoles().contains(applicationRoleService.getRole(ADMIN))
                    || admin.getRoles().contains(applicationRoleService.getRole(OPERATOR))
                    || admin.getRoles().contains(applicationRoleService.getRole(MERCHANT))) {
                Map<String, String> result = new HashMap<>();
                result.put(CASHBOX_CURRENCY_LIST, getField(cashBoxDTO.getCashBoxId(), CASHBOX_CURRENCY_LIST));
                result.put(CASHBOX_PAYSYSTEM_LIST, getField(cashBoxDTO.getCashBoxId(), CASHBOX_PAYSYSTEM_LIST));
                result.put(INTERACTION_URL, getField(cashBoxDTO.getCashBoxId(), INTERACTION_URL));
                result.put(REDIRECT_URL, getField(cashBoxDTO.getCashBoxId(), REDIRECT_URL));
                result.put(REDIRECT_SUCCESS_URL, getField(cashBoxDTO.getCashBoxId(), REDIRECT_SUCCESS_URL));
                result.put(REDIRECT_FAILED_URL, getField(cashBoxDTO.getCashBoxId(), REDIRECT_FAILED_URL));
                result.put(REDIRECT_PENDING_URL, getField(cashBoxDTO.getCashBoxId(), REDIRECT_PENDING_URL));
                result.put(SECRET, getField(cashBoxDTO.getCashBoxId(), SECRET));
                result.put(PERCENT_PAYMENT_SYSTEM, getField(cashBoxDTO.getCashBoxId(), PERCENT_PAYMENT_SYSTEM));
                return new ResultDTO(true, result, 0);
            } else {
                return error121;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

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

    public CashboxSettings getCashboxSettingByFieldNameAndCashboxId(String fieldName, Long cashboxId) {
        CashboxSettings cashboxSettings = cashboxSettingsRepository.findTopByFieldNameAndCashboxId(fieldName, cashboxId);
        if (cashboxSettings == null) {
            cashboxSettings = new CashboxSettings();
            cashboxSettings.setCashboxId(cashboxId);
            cashboxSettings.setFieldName(fieldName);
            cashboxSettings.setFieldValue("");
            cashboxSettingsRepository.save(cashboxSettings);
        }
        return cashboxSettings;
    }
}
