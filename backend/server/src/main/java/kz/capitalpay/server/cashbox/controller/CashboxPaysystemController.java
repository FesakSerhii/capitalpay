package kz.capitalpay.server.cashbox.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.CashboxPaysystemEditListDTO;
import kz.capitalpay.server.cashbox.dto.CashboxRequestDTO;
import kz.capitalpay.server.cashbox.service.CashboxPaysystemService;
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

import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@RestController
@RequestMapping(value = "/api/v1/auth/paysystem", produces = "application/json;charset=UTF-8")
public class CashboxPaysystemController {

    Logger logger = LoggerFactory.getLogger(CashboxPaysystemController.class);

    @Autowired
    Gson gson;

    @Autowired
    CashboxPaysystemService cashboxPaysystemService;

    @Autowired
    ValidationUtil validationUtil;

    @PostMapping("/cashbox/list")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO cashboxList(@RequestBody CashboxRequestDTO dto, Principal principal) {
        logger.info("Cashbox paysystem List");
        logger.info("CashboxRequestDTO: {}", dto);
        return cashboxPaysystemService.findAll(principal, dto);
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
        return validationUtil.handleValidation(ex);
    }
}
