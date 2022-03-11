package kz.capitalpay.server.merchantsettings.service;

import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.dto.CashBoxSettingDTO;
import kz.capitalpay.server.merchantsettings.dto.CashBoxSettingFieldDTO;
import kz.capitalpay.server.merchantsettings.model.CashboxSettings;
import kz.capitalpay.server.merchantsettings.repository.CashboxSettingsRepository;
import kz.capitalpay.server.p2p.model.MerchantP2pSettings;
import kz.capitalpay.server.p2p.service.P2pSettingsService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.payments.service.PaymentService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.login.service.ApplicationRoleService.*;
import static kz.capitalpay.server.merchantsettings.service.MerchantKycService.TOTAL_FEE;

@Service
public class CashboxSettingsService {

    private final CashboxSettingsRepository cashboxSettingsRepository;
    private final MerchantKycService merchantKycService;
    private final ApplicationUserService applicationUserService;
    private final ApplicationRoleService applicationRoleService;
    private final CashboxRepository cashboxRepository;
    private final P2pSettingsService p2pSettingsService;
    private final PaymentService paymentService;

    public static final String CASHBOX_CURRENCY_LIST = "currencylist";
    public static final String CASHBOX_PAYSYSTEM_LIST = "paysystemlist";
    public static final String INTERACTION_URL = "interactionurl";
    public static final String REDIRECT_URL = "redirecturl";
    public static final String REDIRECT_SUCCESS_URL = "redirectsuccess";
    public static final String REDIRECT_FAILED_URL = "redirectfailed";
    public static final String REDIRECT_PENDING_URL = "redirectpending";
    public static final String SECRET = "secret";
    public static final String CLIENT_FEE = "client_fee";

    public CashboxSettingsService(CashboxSettingsRepository cashboxSettingsRepository, MerchantKycService merchantKycService, ApplicationUserService applicationUserService, ApplicationRoleService applicationRoleService, CashboxRepository cashboxRepository, P2pSettingsService p2pSettingsService, PaymentService paymentService) {
        this.cashboxSettingsRepository = cashboxSettingsRepository;
        this.cashboxRepository = cashboxRepository;
        this.merchantKycService = merchantKycService;
        this.applicationUserService = applicationUserService;
        this.applicationRoleService = applicationRoleService;
        this.p2pSettingsService = p2pSettingsService;
        this.paymentService = paymentService;
    }

    public ResultDTO setOrUpdateCashboxSettings(Principal principal, CashBoxSettingDTO request) {
        try {
            ApplicationUser admin = applicationUserService.getUserByLogin(principal.getName());
            if (admin.getRoles().contains(applicationRoleService.getRole(ADMIN))
                    || admin.getRoles().contains(applicationRoleService.getRole(OPERATOR))
                    || admin.getRoles().contains(applicationRoleService.getRole(MERCHANT))) {
                for (CashBoxSettingFieldDTO field : request.getFields()) {
                    if (field.getFieldName().equalsIgnoreCase(CLIENT_FEE)) {
                        ResultDTO resultDTO = savedClientFee(field.getCashBoxId(), field.getFieldName(),
                                field.getFieldValue());
                        if (!resultDTO.isResult()) {
                            return resultDTO;
                        }
                        continue;
                    }
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
                result.put(CLIENT_FEE, getField(cashBoxDTO.getCashBoxId(), CLIENT_FEE));
                return new ResultDTO(true, result, 0);
            } else {
                return error121;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO getPublicCashboxSettings(CashBoxSettingDTO cashBoxDTO) {
        return getPublicCashBoxSettings(cashBoxDTO.getCashBoxId());
    }

    public ResultDTO getPublicCashboxSettings(String p2pPaymentId) {
        Payment payment = paymentService.findById(p2pPaymentId);
        if (Objects.isNull(payment)) {
            return error118;
        }
        return getPublicCashBoxSettings(payment.getCashboxId());
    }

    public ResultDTO deleteCashboxSettings(Principal principal, CashBoxSettingFieldDTO request) {
        try {
            ApplicationUser admin = applicationUserService.getUserByLogin(principal.getName());
            if (admin.getRoles().contains(applicationRoleService.getRole(ADMIN))
                    || admin.getRoles().contains(applicationRoleService.getRole(OPERATOR))) {
                CashboxSettings cashboxSettings = cashboxSettingsRepository.findTopByFieldNameAndCashboxId(request.getFieldName(),
                        request.getCashBoxId());
                cashboxSettingsRepository.delete(cashboxSettings);
                return new ResultDTO(true, "settings was deleted", 0);
            } else {
                return error120;
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

    public ResultDTO savedClientFee(Long cashboxId, String fieldName, String fieldValue) {
        Cashbox cashbox = cashboxRepository.findById(cashboxId).orElse(null);
        double total_fee = Double.parseDouble(merchantKycService.getField(cashbox.getMerchantId(), TOTAL_FEE));
        if (Double.parseDouble(fieldValue) > total_fee) {
            return error127;
        }
        if (fieldValue.trim().isEmpty()) {
            fieldValue = "0.0";
        }
        String pattern = "#.###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        fieldValue = decimalFormat.format(Double.parseDouble(fieldValue)).replace(",", ".");
        setField(cashboxId, fieldName, fieldValue);
        return new ResultDTO(true, "Client fee saved!", 0);
    }

    private ResultDTO getPublicCashBoxSettings(Long cashBoxId) {
        try {
            Cashbox cashbox = cashboxRepository.findById(cashBoxId).orElse(null);
            if (Objects.isNull(cashbox)) {
                return error113;
            }
            MerchantP2pSettings merchantP2pSettings = p2pSettingsService.findP2pSettingsByMerchantId(cashbox.getMerchantId());
            Map<String, Object> result = new HashMap<>();
            result.put(INTERACTION_URL, getField(cashBoxId, INTERACTION_URL));
            result.put(REDIRECT_URL, getField(cashBoxId, REDIRECT_URL));
            result.put(REDIRECT_SUCCESS_URL, getField(cashBoxId, REDIRECT_SUCCESS_URL));
            result.put(REDIRECT_FAILED_URL, getField(cashBoxId, REDIRECT_FAILED_URL));
            result.put(REDIRECT_PENDING_URL, getField(cashBoxId, REDIRECT_PENDING_URL));
            result.put("p2pEnabled", Objects.nonNull(merchantP2pSettings) && merchantP2pSettings.isP2pAllowed() && cashbox.isP2pAllowed());
            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public Map<String, String> getMerchantResultUrls(Long cashBoxId) {
        Cashbox cashbox = cashboxRepository.findById(cashBoxId).orElse(null);
        if (Objects.isNull(cashbox)) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        result.put(REDIRECT_SUCCESS_URL, getField(cashBoxId, REDIRECT_SUCCESS_URL));
        result.put(REDIRECT_FAILED_URL, getField(cashBoxId, REDIRECT_FAILED_URL));
        result.put(REDIRECT_PENDING_URL, getField(cashBoxId, REDIRECT_PENDING_URL));
        return result;
    }
}
