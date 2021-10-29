package kz.capitalpay.server.paysystems.systems.halyksoap.service;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.HalykDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.HalykFieldsDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykSettings;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykSettingsRepository;
import kz.capitalpay.server.validation.BinIinValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static kz.capitalpay.server.constants.ErrorDictionary.error120;
import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;
import static kz.capitalpay.server.login.service.ApplicationRoleService.OPERATOR;

@Service
public class HalykSettingsService {
    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    private ApplicationRoleService applicationRoleService;

    @Autowired
    private BinIinValidatorService binIinValidatorService;

    @Autowired
    private HalykSettingsRepository halykSettingsRepository;

    public static final String KOBD = "kobd";
    public static final String LSKOR = "lskor";
    public static final String RNNB = "rnnb";
    public static final String POLUCH = "poluch";
    public static final String BIK_HALYK = "poluch";

    public static final String NAZNPL_MERCH = "naznpl_merch";
    public static final String BCLASSD_MERCH = "bclassd_merch";
    public static final String KOD_MERCH = "kod_merch";
    public static final String KNP_MERCH = "knp_merch";
    public static final String RNNA_MERCH = "rnna_merch";
    public static final String PLATEL_MERCH = "platel_merch";

    public static final String NAZNPL = "naznpl";
    public static final String BCLASSD = "bclassd";
    public static final String KOD = "kod";
    public static final String KNP = "knp";
    public static final String RNNA = "rnna";
    public static final String PLATEL = "platel";
    public static final String ORDER_NUMBER_REPORT = "order_number";
    public static final String DATE_LAST_DOWNLOADS = "date_last_downloads";

    public ResultDTO setOrUpdateHalykSettings(Principal principal, HalykDTO request) {
        try {
            ApplicationUser admin = applicationUserService.getUserByLogin(principal.getName());
            if (admin.getRoles().contains(applicationRoleService.getRole(ADMIN))
                    || admin.getRoles().contains(applicationRoleService.getRole(OPERATOR))) {
                for (HalykFieldsDTO field : request.getFields()) {
                    if (field.getFieldName().equalsIgnoreCase(RNNA) || field.getFieldName().equalsIgnoreCase(RNNA_MERCH)
                            || field.getFieldName().equalsIgnoreCase(RNNB)) {
                        ResultDTO resultCheck = binIinValidatorService.checkBinIin(field.getFieldValue());
                        if (!resultCheck.isResult()) {
                            return resultCheck;
                        }
                    }
                    setField(field);
                }
                return new ResultDTO(true, request.getFields(), 0);
            } else {
                return error120;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private void setField(HalykFieldsDTO field) {
        HalykSettings halykSettings = halykSettingsRepository.
                findTopByFieldName(field.getFieldName());
        if (halykSettings == null) {
            halykSettings = new HalykSettings();
            halykSettings.setFieldName(field.getFieldName());
        }
        halykSettings.setFieldValue(field.getFieldValue());
        halykSettingsRepository.save(halykSettings);
    }

    public ResultDTO getHalykSettings(Principal principal) {
        try {
            ApplicationUser admin = applicationUserService.getUserByLogin(principal.getName());
            if (admin.getRoles().contains(applicationRoleService.getRole(ADMIN))
                    || admin.getRoles().contains(applicationRoleService.getRole(OPERATOR))) {
                Map<String, String> result = new HashMap<>();
                result.put(KOBD, getFieldValue(KOBD));
                result.put(BIK_HALYK, getFieldValue(BIK_HALYK));
                result.put(LSKOR, getFieldValue(LSKOR));
                result.put(RNNB, getFieldValue(RNNB));
                result.put(POLUCH, getFieldValue(POLUCH));
                result.put(NAZNPL_MERCH, getFieldValue(NAZNPL_MERCH));
                result.put(BCLASSD_MERCH, getFieldValue(BCLASSD_MERCH));
                result.put(KOD_MERCH, getFieldValue(KOD_MERCH));
                result.put(KNP_MERCH, getFieldValue(KNP_MERCH));
                result.put(RNNA_MERCH, getFieldValue(RNNA_MERCH));
                result.put(PLATEL_MERCH, getFieldValue(PLATEL_MERCH));
                result.put(NAZNPL, getFieldValue(NAZNPL));
                result.put(BCLASSD, getFieldValue(BCLASSD));
                result.put(KOD, getFieldValue(KOD));
                result.put(KNP, getFieldValue(KNP));
                result.put(RNNA, getFieldValue(RNNA));
                result.put(PLATEL, getFieldValue(PLATEL));
                result.put(ORDER_NUMBER_REPORT, getFieldValue(ORDER_NUMBER_REPORT));
                result.put(DATE_LAST_DOWNLOADS, getFieldValue(DATE_LAST_DOWNLOADS));
                return new ResultDTO(true, result, 0);
            } else {
                return error120;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public String getFieldValue(String fieldName) {
        HalykSettings halykSettings = halykSettingsRepository.findTopByFieldName(fieldName);
        if (halykSettings == null) {
            halykSettings = new HalykSettings();
            halykSettings.setFieldName(fieldName);
            halykSettings.setFieldValue("");
            halykSettingsRepository.save(halykSettings);
        }
        return halykSettings.getFieldValue();
    }

    public HalykSettings getHalykSettingByFieldName(String fieldName) {
        return halykSettingsRepository.findTopByFieldName(fieldName);
    }

    public HalykSettings saveHalykSettings(HalykSettings halykSettings) {
        return halykSettingsRepository.save(halykSettings);
    }
}
