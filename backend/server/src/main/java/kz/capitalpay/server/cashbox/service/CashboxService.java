package kz.capitalpay.server.cashbox.service;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.CashboxCreateRequestDTO;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.repository.CashboxRepository;
import kz.capitalpay.server.currency.service.CurrencyService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

import static kz.capitalpay.server.constants.ErrorDictionary.error112;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.CASHBOX_CURRENCY_LIST;

@Service
public class CashboxService {

    Logger logger = LoggerFactory.getLogger(CashboxService.class);


    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CashboxSettingsService cashboxSettingsService;

    @Autowired
    CashboxRepository cashboxRepository;

    public ResultDTO createNew(Principal principal, CashboxCreateRequestDTO request) {
        try {
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            Cashbox cashbox = new Cashbox();
            cashbox.setMerchantId(owner.getId());
            cashbox.setName(request.getName());

            cashboxRepository.save(cashbox);

            if (currencyService.checkEnable(request.getCurrency())) {
                cashboxSettingsService.setField(cashbox.getId(), CASHBOX_CURRENCY_LIST, gson.toJson(List.of(request.getCurrency())));
            } else {
                cashboxRepository.delete(cashbox);
                return error112;
            }
            return new ResultDTO(true, cashbox, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
