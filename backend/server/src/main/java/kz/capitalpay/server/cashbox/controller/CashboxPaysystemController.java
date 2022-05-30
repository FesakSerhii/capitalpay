package kz.capitalpay.server.cashbox.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.CashboxPaysystemEditListDTO;
import kz.capitalpay.server.cashbox.dto.CashboxRequestDTO;
import kz.capitalpay.server.cashbox.service.CashboxPaysystemService;
import kz.capitalpay.server.dto.ResultDTO;
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

import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@RestController
@RequestMapping(value = "/api/v1/auth/paysystem", produces = "application/json;charset=UTF-8")
public class CashboxPaysystemController {

    Logger logger = LoggerFactory.getLogger(CashboxPaysystemController.class);

    @Autowired
    Gson gson;

    @Autowired
    CashboxPaysystemService cashboxPaysystemService;

    @PostMapping("/cashbox/list")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO cashboxList(@RequestBody CashboxRequestDTO request, Principal principal) {
        logger.info("Cashbox paysystem List");
        return cashboxPaysystemService.findAll(principal, request);
    }

    @PostMapping("/cashbox/edit")
    @RolesAllowed({MERCHANT})
    ResultDTO cashboxEditList(@Valid @RequestBody CashboxPaysystemEditListDTO request, Principal principal) {
        logger.info("Cashbox Edit List");
        return cashboxPaysystemService.editList(principal, request);
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
