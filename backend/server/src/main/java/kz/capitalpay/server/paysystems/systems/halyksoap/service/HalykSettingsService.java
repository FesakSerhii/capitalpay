package kz.capitalpay.server.paysystems.systems.halyksoap.service;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.HalykDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.HalykFieldsDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykSettings;
import kz.capitalpay.server.paysystems.systems.halyksoap.repository.HalykSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@Service
public class HalykSettingsService {
    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    ApplicationRoleService applicationRoleService;

    @Autowired
    HalykSettingsRepository halykSettingsRepository;

    private static final String KOBD = "kobd";
    private static final String LSKOR = "lskor";
    private static final String RNNB = "rnnb";
    private static final String AMOUNT = "amount";
    private static final String POLUCH = "poluch";
    private static final String NAZNPL_MERCH = "naznpl_merch";
    private static final String BCLASSD_MERCH = "bclassd_merch";
    private static final String KOD_MERCH = "kod_merch";
    private static final String KNP_MERCH = "knp_merch";
    private static final String RNNA_MERCH = "rnna_merch";
    private static final String PLATEL_MERCH = "platel_merch";
    private static final String NAZNPL = "naznpl";
    private static final String BCLASSD = "bclassd";
    private static final String KOD = "kod";
    private static final String KNP = "knp";
    private static final String RNNA = "rnna";
    private static final String PLATEL = "platel";
    private static final String ORDER_NUMBER_REPORT = "order_number";
    private static final String DATE_LAST_DOWNLOADS = "date_last_downloads";

    public ResultDTO setHalykSettings(Principal principal, HalykDTO request) {
        try {
            ApplicationUser admin = applicationUserService.getUserByLogin(principal.getName());
            if (!admin.getRoles().contains(applicationRoleService.getRole(ADMIN))
                    || !admin.getRoles().contains(applicationRoleService.getRole(OPERATOR))) {
                return error120;
            }
            for (HalykFieldsDTO field : request.getFields()) {
                setField(admin.getId(), field);
            }
            return new ResultDTO(true, request.getFields(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private void setField(Long adminId, HalykFieldsDTO field) {
        HalykSettings halykSettings = halykSettingsRepository.
                findTopByFieldNameAndId(field.getFieldName(), adminId);
        if (halykSettings == null) {
            halykSettings = new HalykSettings();
            halykSettings.setFieldName(field.getFieldName());
            halykSettings.setId(adminId);
        }
        halykSettings.setFieldValue(field.getFieldValue());
        halykSettingsRepository.save(halykSettings);
    }

    public ResultDTO getHalykSettings(Principal principal, HalykDTO request) {
        try {
            ApplicationUser admin = applicationUserService.getUserByLogin(principal.getName());
            if (!admin.getRoles().contains(applicationRoleService.getRole(ADMIN))
                    || !admin.getRoles().contains(applicationRoleService.getRole(OPERATOR))) {
                return error120;
            }
            Map<String, String> result = new HashMap<>();
            result.put(KOBD, getField(admin.getId(), KOBD));
            result.put(LSKOR, getField(admin.getId(), LSKOR));
            result.put(RNNB, getField(admin.getId(), RNNB));
            result.put(AMOUNT, getField(admin.getId(), AMOUNT));
            result.put(POLUCH, getField(admin.getId(), POLUCH));
            result.put(NAZNPL_MERCH, getField(admin.getId(), NAZNPL_MERCH));
            result.put(BCLASSD_MERCH, getField(admin.getId(), BCLASSD_MERCH));
            result.put(KOD_MERCH, getField(admin.getId(), KOD_MERCH));
            result.put(KNP_MERCH, getField(admin.getId(), KNP_MERCH));
            result.put(RNNA_MERCH, getField(admin.getId(), RNNA_MERCH));
            result.put(PLATEL_MERCH, getField(admin.getId(), PLATEL_MERCH));
            result.put(NAZNPL, getField(admin.getId(), NAZNPL));
            result.put(BCLASSD, getField(admin.getId(), BCLASSD));
            result.put(KOD, getField(admin.getId(), KOD));
            result.put(KNP, getField(admin.getId(), KNP));
            result.put(RNNA, getField(admin.getId(), RNNA));
            result.put(PLATEL, getField(admin.getId(), PLATEL));

            result.put(ORDER_NUMBER_REPORT, getField(admin.getId(), ORDER_NUMBER_REPORT));
            result.put(DATE_LAST_DOWNLOADS, getField(admin.getId(), DATE_LAST_DOWNLOADS));
            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public String getField(Long adminId, String fieldName) {
        HalykSettings halykSettings = halykSettingsRepository.findTopByFieldNameAndId(fieldName, adminId);
        if (halykSettings == null) {
            halykSettings = new HalykSettings();
            halykSettings.setId(adminId);
            halykSettings.setFieldName(fieldName);
            halykSettings.setFieldValue("");
            halykSettingsRepository.save(halykSettings);
        }
        return halykSettings.getFieldValue();
    }
}
