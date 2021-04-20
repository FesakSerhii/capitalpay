package kz.capitalpay.server.cashbox.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.CashboxCreateRequestDTO;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import java.security.Principal;

import static kz.capitalpay.server.login.service.ApplicationRoleService.MERCHANT;

@RestController
@RequestMapping(value = "/api/v1/cashbox", produces = "application/json;charset=UTF-8")
public class CashboxController {


    Logger logger = LoggerFactory.getLogger(CashboxController.class);

    @Autowired
    Gson gson;

    @Autowired
    CashboxService cashboxService;

    @PostMapping("/create")
    @RolesAllowed({MERCHANT})
    ResultDTO cashboxCreate(@Valid @RequestBody CashboxCreateRequestDTO request, Principal principal) {
        logger.info("Cashbox Create");
        return cashboxService.createNew(principal, request);
    }


}
