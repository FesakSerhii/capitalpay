package kz.capitalpay.server.currency.service;

import com.google.gson.Gson;
import kz.capitalpay.server.currency.dto.AddCurrencyDTO;
import kz.capitalpay.server.currency.dto.EditCurrencyDTO;
import kz.capitalpay.server.currency.model.SystemCurrency;
import kz.capitalpay.server.currency.repository.CurrencyRepository;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.model.OperatorsAction;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

import static kz.capitalpay.server.constants.ErrorDictionary.error111;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.ADD_CURRENCY;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.EDIT_ONE_CURRENCY;

@Service
public class CurrencyService {


    Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    @Autowired
    Gson gson;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;

    public ResultDTO systemList() {
        try {
            List<SystemCurrency> currencyList = currencyRepository.findAll();
            if (currencyList == null || currencyList.size() == 0) {
                SystemCurrency currency = new SystemCurrency();
                currency.setAlpha("USD");
                currency.setName("US Dollar");
                currency.setNumber("840");
                currency.setUnicode("$");
                currencyRepository.save(currency);
                currencyList.add(currency);
            }
            return new ResultDTO(true, currencyList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO editOneCurrency(Principal principal, EditCurrencyDTO request) {
        try {
            SystemCurrency currency = currencyRepository.findByAlpha(request.getAlpha());
            if (request.getName() != null) {
                currency.setName(request.getName());
            }
            if (request.getNumber() != null) {
                currency.setNumber(request.getNumber());
            }
            if (request.getUnicode() != null) {
                currency.setUnicode(request.getUnicode());
            }
            currency.setEnabled(request.getEnabled());

            ApplicationUser operator = applicationUserService.getUserByLogin(principal.getName());

            systemEventsLogsService.addNewOperatorAction(operator.getUsername(), EDIT_ONE_CURRENCY,
                    gson.toJson(request), "all");

            currencyRepository.save(currency);
            return new ResultDTO(true, currency, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO addCurrency(Principal principal, AddCurrencyDTO request) {
        try {
            SystemCurrency currency = currencyRepository.findByAlpha(request.getAlpha());
            if (currency != null) {
                return error111;
            } else {
                currency = new SystemCurrency();
            }

            currency.setName(request.getName());

            currency.setNumber(request.getNumber());

            currency.setUnicode(request.getUnicode());

            currency.setEnabled(request.getEnabled());

            ApplicationUser operator = applicationUserService.getUserByLogin(principal.getName());

            systemEventsLogsService.addNewOperatorAction(operator.getUsername(), ADD_CURRENCY,
                    gson.toJson(request), "all");

            currencyRepository.save(currency);
            return new ResultDTO(true, currency, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
