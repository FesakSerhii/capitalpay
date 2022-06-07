package kz.capitalpay.server.paysystems.controller;


import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paysystems.dto.ActivatePaysystemDTO;
import kz.capitalpay.server.paysystems.service.PaysystemService;
import kz.capitalpay.server.util.ValidationUtil;
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
public class PaysystemController {

    Logger logger = LoggerFactory.getLogger(PaysystemController.class);

    @Autowired
    Gson gson;

    @Autowired
    PaysystemService paysystemService;

    @Autowired
    ValidationUtil validationUtil;

    @PostMapping("/system/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO paysystemList() {
        logger.info("Paysystem List");
        return paysystemService.systemList();
    }


    @PostMapping("/system/enable")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO enablePaysystem(@Valid @RequestBody ActivatePaysystemDTO request, Principal principal) {
        logger.info("Enable paysystem");
        return paysystemService.enablePaysystem(principal, request);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
