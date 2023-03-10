package kz.capitalpay.server.currency.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.currency.dto.MerchantEditListDTO;
import kz.capitalpay.server.currency.dto.MerchantRequestDTO;
import kz.capitalpay.server.currency.service.MerchantCurrencyService;
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
public class MerchantCurrencyController {

    Logger logger = LoggerFactory.getLogger(MerchantCurrencyController.class);

    @Autowired
    Gson gson;

    @Autowired
    MerchantCurrencyService merchantCurrencyService;

    @Autowired
    ValidationUtil validationUtil;

    @PostMapping("/merchant/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO merchantList(@RequestBody MerchantRequestDTO request) {
        logger.info("Merchant List");
        return merchantCurrencyService.findAll(request);
    }

    @PostMapping("/merchant/edit")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO merchantEditList(@Valid @RequestBody MerchantEditListDTO request, Principal principal) {
        logger.info("Merchant Edit List");
        return merchantCurrencyService.editList(principal, request);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
