package kz.capitalpay.server.merchantsettings.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.merchantsettings.dto.MerchantKycDTO;
import kz.capitalpay.server.merchantsettings.service.MerchantKycService;
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
@RequestMapping(value = "/api/v1/merchantsettings", produces = "application/json;charset=UTF-8")
public class MerchantKycController {

    Logger logger = LoggerFactory.getLogger(MerchantKycController.class);

    @Autowired
    Gson gson;

    @Autowired
    MerchantKycService merchantKycService;


    @PostMapping("/kyc/set")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO setKyc(@Valid @RequestBody MerchantKycDTO request, Principal principal) {
        logger.info("KYC set");
        return merchantKycService.setKyc(principal, request);
    }


    @PostMapping("/kyc/get")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO getKyc(@Valid @RequestBody MerchantKycDTO request, Principal principal) {
        logger.info("KYC get");
        return merchantKycService.getKyc(principal, request);
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
