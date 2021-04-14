package kz.capitalpay.server.merchantsettings.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.dto.MerchantKycDTO;
import kz.capitalpay.server.merchantsettings.dto.MerchantKycFieldDTO;
import kz.capitalpay.server.merchantsettings.model.MerchantKyc;
import kz.capitalpay.server.merchantsettings.repository.MerchantKycRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.EDIT_MERCHANT_KYC;
import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@Service
public class MerchantKycService {

    Logger logger = LoggerFactory.getLogger(MerchantKycService.class);

    @Autowired
    Gson gson;

    @Autowired
    MerchantKycRepository merchantKycRepository;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    ApplicationRoleService applicationRoleService;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;

    public static final String IINBIN = "iinbin";
    public static final String MNAME = "mname";
    public static final String UADDRESS = "uaddress";
    public static final String FADDRESS = "faddress";
    public static final String HEADNAME = "headname";
    public static final String ACCOUNTANT = "accountant";
    public static final String MAINPHONE = "mainphone";
    public static final String BANKNAME = "bankname";
    public static final String IIK = "iik";
    public static final String BIK = "bik";


    public ResultDTO setKyc(Principal principal, MerchantKycDTO request) {
        try {

            ApplicationUser applicationUser = applicationUserService.getUserById(request.getMerchantId());
            if (applicationUser == null) {
                return error106;
            }

            if (!applicationUser.getRoles().contains(applicationRoleService.getRole(MERCHANT))) {
                return error108;
            }

            for (MerchantKycFieldDTO field : request.getFields()) {
                setField(applicationUser.getId(), field);
            }

            ApplicationUser admin = applicationUserService.getUserByLogin(principal.getName());

            systemEventsLogsService.addNewOperatorAction(admin.getUsername(), EDIT_MERCHANT_KYC,
                    gson.toJson(request), applicationUser.getId().toString());

            return new ResultDTO(true, request.getFields(), 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private void setField(Long merchantId, MerchantKycFieldDTO field) {
        MerchantKyc merchantKyc = merchantKycRepository.
                findTopByFieldNameAndMerchantId(field.getFieldName(), merchantId);
        if (merchantKyc == null) {
            merchantKyc = new MerchantKyc();
            merchantKyc.setFieldName(field.getFieldName());
            merchantKyc.setMerchantId(merchantId);
        }
        merchantKyc.setFieldValue(field.getFieldValue());
        merchantKycRepository.save(merchantKyc);
    }

    public ResultDTO getKyc(Principal principal, MerchantKycDTO request) {
        try {
            ApplicationUser merchant = applicationUserService.getUserById(request.getMerchantId());
            if (merchant == null) {
                return error106;
            }

            ApplicationUser operator = applicationUserService.getUserByLogin(principal.getName());
            if (!operator.getRoles().contains(applicationRoleService.getRole(ADMIN)) &&
                    !operator.getRoles().contains(applicationRoleService.getRole(OPERATOR)) &&
                    !operator.getId().equals(merchant.getId())) {
                return error110;
            }

            if (!merchant.getRoles().contains(applicationRoleService.getRole(MERCHANT))) {
                return error108;
            }

            Map<String, String> result = new HashMap<>();
            result.put(IINBIN, getField(merchant.getId(), IINBIN));
            result.put(MNAME, getField(merchant.getId(), MNAME));
            result.put(UADDRESS, getField(merchant.getId(), UADDRESS));
            result.put(FADDRESS, getField(merchant.getId(), FADDRESS));
            result.put(HEADNAME, getField(merchant.getId(), HEADNAME));
            result.put(ACCOUNTANT, getField(merchant.getId(), ACCOUNTANT));
            result.put(MAINPHONE, getField(merchant.getId(), MAINPHONE));
            result.put(BANKNAME, getField(merchant.getId(), BANKNAME));
            result.put(IIK, getField(merchant.getId(), IIK));
            result.put(BIK, getField(merchant.getId(), BIK));
            logger.info(gson.toJson(result));
            return new ResultDTO(true, result, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private String getField(Long id, String fieldName) {
        MerchantKyc merchantKyc = merchantKycRepository.findTopByFieldNameAndMerchantId(fieldName, id);
        if (merchantKyc == null) {
            merchantKyc = new MerchantKyc();
            merchantKyc.setMerchantId(id);
            merchantKyc.setFieldName(fieldName);
            merchantKyc.setFieldValue("");
            merchantKycRepository.save(merchantKyc);
        }
        return merchantKyc.getFieldValue();
    }
}
