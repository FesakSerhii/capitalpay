package kz.capitalpay.server.cashbox.controller;

import kz.capitalpay.server.cashbox.dto.CashBoxFeeDto;
import kz.capitalpay.server.cashbox.service.CashboxFeeService;
import kz.capitalpay.server.dto.ResultDTO;
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
@RequestMapping(value = "/api/v1/fee", produces = "application/json;charset=UTF-8")
public class CashboxFeeController {

    @Autowired
    CashboxFeeService cashboxFeeService;

    @PostMapping("/cashbox/list")
    @RolesAllowed({MERCHANT})
    ResultDTO listCashBoxByFee(Principal principal) {
        return cashboxFeeService.getCashBoxFeeList(principal);
    }

    @PostMapping("/cashbox/list/save")
    @RolesAllowed({MERCHANT})
    ResultDTO saveListCashBoxFee(@Valid @RequestBody CashBoxFeeDto cashBoxFeeDto, Principal principal) {
        return cashboxFeeService.saveCashBoxFeeList(cashBoxFeeDto, principal);
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
