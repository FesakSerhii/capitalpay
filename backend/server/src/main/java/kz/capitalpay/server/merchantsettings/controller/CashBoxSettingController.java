package kz.capitalpay.server.merchantsettings.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.merchantsettings.dto.CashBoxSettingDTO;
import kz.capitalpay.server.merchantsettings.dto.CashBoxSettingFieldDTO;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@RestController
@RequestMapping(value = "/api/v1/auth/cashboxsetting", produces = "application/json;charset=UTF-8")
public class CashBoxSettingController {

    @Autowired
    private CashboxSettingsService cashboxSettingsService;

    @Autowired
    private ValidationUtil validationUtil;

    @PostMapping("/delete")
    @RolesAllowed({ADMIN, OPERATOR})
    ResultDTO deleteCashBoxSettings(@Valid @RequestBody CashBoxSettingFieldDTO request, Principal principal) {
        return cashboxSettingsService.deleteCashboxSettings(principal, request);
    }

    @PostMapping("/set")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO setCashBoxSettings(@Valid @RequestBody CashBoxSettingDTO request, Principal principal) {
        return cashboxSettingsService.setOrUpdateCashboxSettings(principal, request);
    }

    @PostMapping("/get")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO getCashBoxSettings(@Valid @RequestBody CashBoxSettingDTO request, Principal principal) {
        return cashboxSettingsService.getCashboxSettings(principal, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
