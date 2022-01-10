package kz.capitalpay.server.merchantsettings.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.merchantsettings.dto.CashBoxSettingDTO;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
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
@RequestMapping(value = "/api/v1/cashboxsetting", produces = "application/json;charset=UTF-8")
public class CashBoxPublicSettingsController {

    @Autowired
    private CashboxSettingsService cashboxSettingsService;

    @PostMapping("/get")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO getCashBoxSettings(@Valid @RequestBody CashBoxSettingDTO request, Principal principal) {
        return cashboxSettingsService.getPublicCashboxSettings(principal, request);
    }
}
