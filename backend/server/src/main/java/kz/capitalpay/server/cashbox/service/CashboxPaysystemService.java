package kz.capitalpay.server.cashbox.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.CashboxCurrencyEditListDTO;
import kz.capitalpay.server.cashbox.dto.CashboxPaysystemEditListDTO;
import kz.capitalpay.server.cashbox.dto.CashboxRequestDTO;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.currency.model.SystemCurrency;
import kz.capitalpay.server.currency.service.CurrencyService;
import kz.capitalpay.server.currency.service.MerchantCurrencyService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationRoleService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.paysystems.dto.PaySystemListDTO;
import kz.capitalpay.server.paysystems.model.Paysystem;
import kz.capitalpay.server.paysystems.service.MerchantPaysystemService;
import kz.capitalpay.server.paysystems.service.PaysystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;
import static kz.capitalpay.server.login.service.ApplicationRoleService.OPERATOR;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.CASHBOX_CURRENCY_LIST;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.CASHBOX_PAYSYSTEM_LIST;

@Service
public class CashboxPaysystemService {

    Logger logger = LoggerFactory.getLogger(CashboxPaysystemService.class);


    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    ApplicationRoleService applicationRoleService;

    @Autowired
    CashboxRepository cashboxRepository;

    @Autowired
    CashboxSettingsService cashboxSettingsService;

    @Autowired
    PaysystemService paysystemService;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;

    @Autowired
    MerchantPaysystemService merchantPaysystemService;

    public ResultDTO findAll(Principal principal, CashboxRequestDTO request) {
        try {
            Cashbox cashbox = cashboxRepository.findById(request.getCashboxId()).orElse(null);
            if (cashbox == null) {
                return error113;
            }

            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());

            if (!owner.getRoles().contains(applicationRoleService.getRole(OPERATOR)) &&
                    !owner.getRoles().contains(applicationRoleService.getRole(ADMIN)) &&
                    !cashbox.getMerchantId().equals(owner.getId())) {
                return error110;
            }

            String paysystemJson = cashboxSettingsService.getField(cashbox.getId(), CASHBOX_PAYSYSTEM_LIST);
            List<Long> paysystemList = new ArrayList<>();

            if (paysystemJson != null && paysystemJson.length() > 0) {
                List<Double> doubleList = gson.fromJson(paysystemJson, List.class);
                doubleList.forEach(aDouble -> paysystemList.add(aDouble.longValue()));
                logger.info(gson.toJson(paysystemList));
            }
            Set<Long> paysystemSet = new HashSet<>(paysystemList);
            logger.info("Set: {}", gson.toJson(paysystemSet));
            List<Paysystem> systemPaysystemList = merchantPaysystemService.paysystemList(owner.getId());
            List<PaySystemListDTO> result = new ArrayList<>();
            for (Paysystem ps : systemPaysystemList) {
                PaySystemListDTO paySystemListDTO = new PaySystemListDTO();
                paySystemListDTO.setId(ps.getId());
                paySystemListDTO.setName(ps.getName());
                paySystemListDTO.setEnabled(paysystemSet.contains(ps.getId()));

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
                return error113;
            }

            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());

            if (!cashbox.getMerchantId().equals(owner.getId())) {
                return error110;
            }

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

            cashboxSettingsService.setField(cashbox.getId(), CASHBOX_PAYSYSTEM_LIST, paysystemJson);
            return new ResultDTO(true, request.getPaysystemList(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
