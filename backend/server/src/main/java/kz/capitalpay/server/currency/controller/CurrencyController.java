package kz.capitalpay.server.currency.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.currency.dto.AddCurrencyDTO;
import kz.capitalpay.server.currency.dto.EditCurrencyDTO;
import kz.capitalpay.server.currency.service.CurrencyService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api/v1/auth/currency", produces = "application/json;charset=UTF-8")
public class CurrencyController {

    Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    @Autowired
    Gson gson;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    ValidationUtil validationUtil;


    @PostMapping("/system/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO systemList() {
        logger.info("System List");
        return currencyService.systemList();
    }

    @PostMapping("/system/edit")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO editOneCurrency(@Valid @RequestBody EditCurrencyDTO request, Principal principal) {
        logger.info("Edit One Currency");
        return currencyService.editOneCurrency(principal, request);
    }

    @PostMapping("/system/add")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO addCurrency(@Valid @RequestBody AddCurrencyDTO request, Principal principal) {
        logger.info("Add Currency");
        return currencyService.addCurrency(principal, request);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
