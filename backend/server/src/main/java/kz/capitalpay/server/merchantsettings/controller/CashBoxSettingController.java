package kz.capitalpay.server.merchantsettings.controller;

import kz.capitalpay.server.merchantsettings.dto.CashBoxSettingDTO;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;
import static kz.capitalpay.server.login.service.ApplicationRoleService.OPERATOR;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.PERCENT_PAYMENT_SYSTEM;

@RestController
@RequestMapping(value = "/api/v1/cashboxsetting", produces = "application/json;charset=UTF-8")
public class CashBoxSettingController {

    @Autowired
    private CashboxSettingsService cashboxSettingsService;

    @ResponseBody
    @RolesAllowed({ADMIN, OPERATOR})
    @PostMapping(value = "/percent", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Boolean setPercentField(@RequestBody CashBoxSettingDTO cashBoxSettingDTO, Principal principal) {
        cashboxSettingsService.setField(cashBoxSettingDTO.getCashBoxId(), PERCENT_PAYMENT_SYSTEM,
                cashBoxSettingDTO.getFieldValue());
        return true;
    }
}
