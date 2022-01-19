package kz.capitalpay.server.paysystems.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.currency.dto.MerchantRequestDTO;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paysystems.dto.MerchantEditListDTO;
import kz.capitalpay.server.paysystems.service.MerchantPaysystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/auth/paysystem", produces = "application/json;charset=UTF-8")
public class MerchantPaysystemController {

    Logger logger = LoggerFactory.getLogger(MerchantPaysystemController.class);

    @Autowired
    Gson gson;

    @Autowired
    MerchantPaysystemService merchantPaysystemService;

    @PostMapping("/merchant/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO merchantList(@RequestBody MerchantRequestDTO request) {
        logger.info("Merchant List");
        return merchantPaysystemService.findAll(request);
    }

    @PostMapping("/merchant/edit")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO merchantEditList(@Valid @RequestBody MerchantEditListDTO request, Principal principal) {
        logger.info("Merchant Edit List");
        return merchantPaysystemService.editList(principal, request);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResultDTO(false, errors, -2);
    }
}
