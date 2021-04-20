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
import kz.capitalpay.server.paysystems.model.Paysystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

import static kz.capitalpay.server.constants.ErrorDictionary.error106;
import static kz.capitalpay.server.constants.ErrorDictionary.error112;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.EDIT_MERCHANT_PAYSYSTEM;
import static kz.capitalpay.server.merchantsettings.service.MerchantSettingsService.MERCHANT_PAYSYSTEM_LIST;

@Service
public class MerchantPaysystemService {

    Logger logger = LoggerFactory.getLogger(MerchantPaysystemService.class);

    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    MerchantSettingsService merchantSettingsService;

    @Autowired
    PaysystemService paysystemService;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;

    public ResultDTO findAll(MerchantRequestDTO request) {
        try {
            ApplicationUser merchant = applicationUserService.getUserById(request.getMerchantId());
            if (merchant == null) {
                return error106;
            }

            String paysystemJson = merchantSettingsService.getField(merchant.getId(), MERCHANT_PAYSYSTEM_LIST);
            logger.info("JSON: {}", paysystemJson);
            List<Long> paysystemList = new ArrayList<>();
            if (paysystemJson != null && paysystemJson.length() > 0) {
                List<Double> doubleList = gson.fromJson(paysystemJson, List.class);
                doubleList.forEach(aDouble -> paysystemList.add(aDouble.longValue()));
                logger.info(gson.toJson(paysystemList));
            }
            logger.info("List: {}", paysystemList);
            Set<Long> paysystemSet = new HashSet<>(paysystemList);
            logger.info("Set: {}", gson.toJson(paysystemSet));
            List<Paysystem> systemPaysystemList = paysystemService.paysystemList();
            List<PaySystemListDTO> result = new ArrayList<>();
            for (Paysystem ps : systemPaysystemList) {
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

    public List<Paysystem> paysystemList(Long merchantId) {
        String paysystemJson = merchantSettingsService.getField(merchantId, MERCHANT_PAYSYSTEM_LIST);
        logger.info("JSON: {}", paysystemJson);
        List<Long> paysystemList = new ArrayList<>();
        if (paysystemJson != null && paysystemJson.length() > 0) {
            List<Double> doubleList = gson.fromJson(paysystemJson, List.class);
            doubleList.forEach(aDouble -> paysystemList.add(aDouble.longValue()));
            logger.info(gson.toJson(paysystemList));
        }
        logger.info("List: {}", paysystemList);
        Set<Long> paysystemSet = new HashSet<>(paysystemList);
        logger.info("Set: {}", gson.toJson(paysystemSet));
        List<Paysystem> systemPaysystemList = paysystemService.paysystemList();
        List<Paysystem> result = new ArrayList<>();
        for (Paysystem ps : systemPaysystemList) {
            if (ps.isEnabled() && paysystemSet.contains(ps.getId())) {
                result.add(ps);
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

            List<Paysystem> systemPaysystemList = paysystemService.paysystemList();

            for (Long l : request.getPaysystemList()) {
                boolean error = true;
                for (Paysystem ps : systemPaysystemList) {
                    if (ps.getId().equals(l) && ps.isEnabled()) {
                        error = false;
                        break;
                    }
                }
                if (error) {
                    return error112;
                }
            }

            String paysystemJson = gson.toJson(request.getPaysystemList());
            logger.info(paysystemJson);

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
