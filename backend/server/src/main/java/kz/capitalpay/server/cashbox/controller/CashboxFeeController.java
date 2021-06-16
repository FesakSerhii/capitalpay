package kz.capitalpay.server.cashbox.controller;

import kz.capitalpay.server.cashbox.service.CashboxFeeService;
import kz.capitalpay.server.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.security.RolesAllowed;
import java.security.Principal;

import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@RestController
@RequestMapping(value = "/api/v1/fee", produces = "application/json;charset=UTF-8")
public class CashboxFeeController {

    @Autowired
    CashboxFeeService cashboxFeeService;

    @PostMapping("/cashbox/list")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO listCashBoxByFee(Principal principal) {
        return cashboxFeeService.getCashBoxFeeList(principal);
    }
}
