package kz.capitalpay.server.cashbox.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.CashboxPaysystemEditListDTO;
import kz.capitalpay.server.cashbox.dto.CashboxRequestDTO;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.paysystems.dto.PaySystemListDTO;
import kz.capitalpay.server.paysystems.model.PaysystemInfo;
import kz.capitalpay.server.paysystems.service.MerchantPaysystemService;
import kz.capitalpay.server.paysystems.service.PaysystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;
import static kz.capitalpay.server.login.service.ApplicationRoleService.OPERATOR;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.CASHBOX_PAYSYSTEM_LIST;

@Service
public class CashboxPaysystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CashboxPaysystemService.class);

    Gson gson;
    ApplicationUserService applicationUserService;
    ApplicationRoleService applicationRoleService;
    CashboxRepository cashboxRepository;
    CashboxSettingsService cashboxSettingsService;
    PaysystemService paysystemService;
    MerchantPaysystemService merchantPaysystemService;

    public ResultDTO findAll(Principal principal, CashboxRequestDTO dto) {
        try {
            Cashbox cashbox = cashboxRepository.findById(dto.getCashboxId()).orElse(null);
            if (cashbox == null) {
                return CASHBOX_NOT_FOUND;
            }
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            if (!owner.getRoles().contains(applicationRoleService.getRole(OPERATOR)) &&
                    !owner.getRoles().contains(applicationRoleService.getRole(ADMIN)) &&
                    !cashbox.getMerchantId().equals(owner.getId())) {
                return NOT_ENOUGH_RIGHTS;
            }
            String paysystemJson = cashboxSettingsService.getField(cashbox.getId(), CASHBOX_PAYSYSTEM_LIST);
            List<Long> paysystemList = new ArrayList<>();

            if (paysystemJson != null && paysystemJson.length() > 0) {
                List<Double> doubleList = gson.fromJson(paysystemJson, List.class);
                doubleList.forEach(aDouble -> paysystemList.add(aDouble.longValue()));
//                logger.info(gson.toJson(paysystemList));
            }

            List<PaysystemInfo> systemPaysystemInfoList = merchantPaysystemService.paysystemList(owner.getId());
            List<PaySystemListDTO> result = new ArrayList<>();
            for (PaysystemInfo ps : systemPaysystemInfoList) {
                PaySystemListDTO paySystemListDTO = new PaySystemListDTO();
                paySystemListDTO.setId(ps.getId());
                paySystemListDTO.setName(ps.getName());
                paySystemListDTO.setEnabled(paysystemList.contains(ps.getId()));
                result.add(paySystemListDTO);
            }
            return new ResultDTO(true, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    public ResultDTO editList(Principal principal, CashboxPaysystemEditListDTO request) {
        try {
            Cashbox cashbox = cashboxRepository.findById(request.getCashboxId()).orElse(null);
            if (cashbox == null) {
                return CASHBOX_NOT_FOUND;
            }
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            if (!cashbox.getMerchantId().equals(owner.getId())) {
                return NOT_ENOUGH_RIGHTS;
            }
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
//            logger.info(paysystemJson);
            cashboxSettingsService.setField(cashbox.getId(), CASHBOX_PAYSYSTEM_LIST, paysystemJson);
            return new ResultDTO(true, request.getPaysystemList(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public List<PaysystemInfo> availablePaysystemList(Long cashboxId) {
        try {
            Cashbox cashbox = cashboxRepository.findById(cashboxId).orElse(null);
            ApplicationUser owner = applicationUserService.getUserById(cashbox.getMerchantId());
            String paysystemJson = cashboxSettingsService.getField(cashboxId, CASHBOX_PAYSYSTEM_LIST);
            List<Long> paysystemList = new ArrayList<>();

            if (paysystemJson != null && paysystemJson.length() > 0) {
                List<Double> doubleList = gson.fromJson(paysystemJson, List.class);
                doubleList.forEach(aDouble -> paysystemList.add(aDouble.longValue()));
//                logger.info(gson.toJson(paysystemList));
            }
            List<PaysystemInfo> systemPaysystemInfoList = merchantPaysystemService.paysystemList(owner.getId());
//            logger.info("systemPaysystemInfoList: {}",gson.toJson(systemPaysystemInfoList));
            List<PaysystemInfo> result = new ArrayList<>();
            for (PaysystemInfo ps : systemPaysystemInfoList) {
//                logger.info(ps.getId().toString());
//                logger.info(String.valueOf(paysystemList.contains(ps.getId())));
                if (paysystemList.contains(ps.getId())) {
                    result.add(ps);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
