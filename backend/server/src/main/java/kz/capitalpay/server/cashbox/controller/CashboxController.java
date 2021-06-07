package kz.capitalpay.server.cashbox.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.dto.CashboxCreateRequestDTO;
import kz.capitalpay.server.cashbox.dto.CashboxNameRequestDTO;
import kz.capitalpay.server.cashbox.dto.CashboxRequestDTO;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.currency.dto.MerchantRequestDTO;
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

import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

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

    @PostMapping("/changename")
    @RolesAllowed({MERCHANT})
    ResultDTO changeName(@Valid @RequestBody CashboxNameRequestDTO request, Principal principal) {
        logger.info("Change Cashbox name");
        return cashboxService.changeName(principal, request);
    }

    @PostMapping("/delete")
    @RolesAllowed({MERCHANT})
    ResultDTO delete(@Valid @RequestBody CashboxRequestDTO request, Principal principal) {
        logger.info("Delete Cashbox");
        return cashboxService.delete(principal, request);
    }


    @PostMapping("/list")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO list(@Valid @RequestBody MerchantRequestDTO request, Principal principal) {
        logger.info("List Cashbox");
        return cashboxService.list(principal, request);
    }

    @PostMapping("/all")
    @RolesAllowed({ADMIN, OPERATOR})
    ResultDTO all() {
        logger.info("All system cashbox");
        return cashboxService.all();
    }


}
