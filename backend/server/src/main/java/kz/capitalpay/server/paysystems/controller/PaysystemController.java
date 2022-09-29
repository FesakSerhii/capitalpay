package kz.capitalpay.server.paysystems.controller;


import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paysystems.dto.ActivatePaysystemDTO;
import kz.capitalpay.server.paysystems.service.PaysystemService;
import kz.capitalpay.server.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api/v1/auth/paysystem", produces = "application/json;charset=UTF-8")
public class PaysystemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaysystemController.class);

    private final PaysystemService paysystemService;
    private final ValidationUtil validationUtil;

    public PaysystemController(PaysystemService paysystemService, ValidationUtil validationUtil) {
        this.paysystemService = paysystemService;
        this.validationUtil = validationUtil;
    }

    @PostMapping("/system/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO paysystemList() {
        LOGGER.info("Paysystem List");
        return paysystemService.systemList();
    }


    @PostMapping("/system/enable")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO enablePaysystem(@Valid @RequestBody ActivatePaysystemDTO request, Principal principal) {
        LOGGER.info("Enable paysystem");
        return paysystemService.enablePaysystem(principal, request);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
