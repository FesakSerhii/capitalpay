package kz.capitalpay.server.merchantsettings.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.merchantsettings.dto.MerchantKycDTO;
import kz.capitalpay.server.merchantsettings.service.MerchantKycService;
import kz.capitalpay.server.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@RestController
@RequestMapping(value = "/api/v1/auth/merchantsettings", produces = "application/json;charset=UTF-8")
public class MerchantKycController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantKycController.class);

    @Autowired
    private Gson gson;

    @Autowired
    private MerchantKycService merchantKycService;

    @Autowired
    private ValidationUtil validationUtil;


    @PostMapping("/kyc/set")
    @RolesAllowed({ADMIN, OPERATOR})
    ResultDTO setKyc(@Valid @RequestBody MerchantKycDTO request, Principal principal) {
        LOGGER.info("KYC set");
        return merchantKycService.setKyc(principal, request);
    }


    @PostMapping("/kyc/get")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO getKyc(@Valid @RequestBody MerchantKycDTO request, Principal principal) {
        LOGGER.info("KYC get");
        return merchantKycService.getKyc(principal, request);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
