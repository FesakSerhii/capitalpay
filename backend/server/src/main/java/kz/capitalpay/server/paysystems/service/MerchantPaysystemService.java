package kz.capitalpay.server.paysystems.service;

import com.google.gson.Gson;
import kz.capitalpay.server.currency.dto.MerchantRequestDTO;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.MerchantSettingsService;
import kz.capitalpay.server.paysystems.dto.MerchantEditListDTO;
import kz.capitalpay.server.paysystems.dto.PaySystemListDTO;
import kz.capitalpay.server.paysystems.model.PaysystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static kz.capitalpay.server.constants.ErrorDictionary.CURRENCY_NOT_FOUND;
import static kz.capitalpay.server.constants.ErrorDictionary.USER_NOT_FOUND;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.EDIT_MERCHANT_PAYSYSTEM;
import static kz.capitalpay.server.merchantsettings.service.MerchantSettingsService.MERCHANT_PAYSYSTEM_LIST;

@Service
public class MerchantPaysystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantPaysystemService.class);

    Gson gson;
    ApplicationUserService applicationUserService;
    MerchantSettingsService merchantSettingsService;
    PaysystemService paysystemService;
    SystemEventsLogsService systemEventsLogsService;

    public ResultDTO findAll(MerchantRequestDTO dto) {
        try {
            LOGGER.info("findAll(MerchantRequestDTO dto)");
            LOGGER.info("dto: {}", dto);
            ApplicationUser merchant = applicationUserService.getUserById(dto.getMerchantId());
            if (merchant == null) {
                return USER_NOT_FOUND;
            }
            String paysystemJson = merchantSettingsService.getField(merchant.getId(), MERCHANT_PAYSYSTEM_LIST);
            LOGGER.info("JSON: {}", paysystemJson);
            List<Long> paysystemList = new ArrayList<>();
            if (paysystemJson != null && paysystemJson.length() > 0) {
                List<Double> doubleList = gson.fromJson(paysystemJson, List.class);
                doubleList.forEach(aDouble -> paysystemList.add(aDouble.longValue()));
                LOGGER.info(gson.toJson(paysystemList));
            }
            LOGGER.info("List: {}", paysystemList);
            Set<Long> paysystemSet = new HashSet<>(paysystemList);
            LOGGER.info("Set: {}", gson.toJson(paysystemSet));
            List<PaysystemInfo> systemPaysystemInfoList = paysystemService.paysystemList();
            List<PaySystemListDTO> result = new ArrayList<>();
            for (PaysystemInfo ps : systemPaysystemInfoList) {
                if (ps.isEnabled()) {
                    PaySystemListDTO paySystemListDTO = new PaySystemListDTO();
                    paySystemListDTO.setId(ps.getId());
                    paySystemListDTO.setName(ps.getName());
                    paySystemListDTO.setEnabled(paysystemSet.contains(ps.getId()));
                    result.add(paySystemListDTO);
                }
            }
            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    public List<PaysystemInfo> paysystemList(Long merchantId) {
        String paysystemJson = merchantSettingsService.getField(merchantId, MERCHANT_PAYSYSTEM_LIST);
        LOGGER.info("JSON: {}", paysystemJson);
        List<Long> paysystemList = new ArrayList<>();
        if (paysystemJson != null && paysystemJson.length() > 0) {
            List<Double> doubleList = gson.fromJson(paysystemJson, List.class);
            doubleList.forEach(aDouble -> paysystemList.add(aDouble.longValue()));
            LOGGER.info(gson.toJson(paysystemList));
        }
        LOGGER.info("List: {}", paysystemList);

        List<PaysystemInfo> systemPaysystemInfoList = paysystemService.paysystemList();
        LOGGER.info("systemPaysystemInfoList: {}", gson.toJson(systemPaysystemInfoList));
        List<PaysystemInfo> result = new ArrayList<>();
        for (PaysystemInfo ps : systemPaysystemInfoList) {
            LOGGER.info("ps.getId(): {}", ps.getId());
            LOGGER.info(String.valueOf(paysystemList.contains(ps.getId())));
            if (ps.isEnabled() && paysystemList.contains(ps.getId())) {
                result.add(ps);
            }
        }
        return result;
    }

    public ResultDTO editList(Principal principal, MerchantEditListDTO request) {
        try {
            ApplicationUser merchant = applicationUserService.getUserById(request.getMerchantId());
            if (merchant == null) {
                return USER_NOT_FOUND;
            }
            ApplicationUser operator = applicationUserService.getUserByLogin(principal.getName());
            List<PaysystemInfo> systemPaysystemInfoList = paysystemService.paysystemList();

            for (Long l : request.getPaysystemList()) {
                boolean error = true;
                for (PaysystemInfo ps : systemPaysystemInfoList) {
                    if (ps.getId().equals(l) && ps.isEnabled()) {
                        error = false;
                        break;
                    }
                }
                if (error) {
                    return CURRENCY_NOT_FOUND;
                }
            }

            String paysystemJson = gson.toJson(request.getPaysystemList());
            LOGGER.info(paysystemJson);
            systemEventsLogsService.addNewOperatorAction(operator.getUsername(),
                    EDIT_MERCHANT_PAYSYSTEM, gson.toJson(request), merchant.getId().toString());
            merchantSettingsService.setField(merchant.getId(), MERCHANT_PAYSYSTEM_LIST, paysystemJson);
            return new ResultDTO(true, request.getPaysystemList(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }
}
