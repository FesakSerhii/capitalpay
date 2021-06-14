package kz.capitalpay.server.merchantsettings.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.merchantsettings.dto.CashBoxSettingDTO;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
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
@RequestMapping(value = "/api/v1/cashboxsetting", produces = "application/json;charset=UTF-8")
public class CashBoxSettingController {

    @Autowired
    private CashboxSettingsService cashboxSettingsService;

    @PostMapping("/set")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO setKyc(@Valid @RequestBody CashBoxSettingDTO request, Principal principal) {
        return cashboxSettingsService.setOrUpdateCashboxSettings(principal, request);
    }

    @PostMapping("/get")
    @RolesAllowed({ADMIN, OPERATOR,MERCHANT})
    ResultDTO getKyc(@Valid @RequestBody CashBoxSettingDTO request, Principal principal) {
        return cashboxSettingsService.getCashboxSettings(principal, request);
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
