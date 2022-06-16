package kz.capitalpay.server.paysystems.controller;

import kz.capitalpay.server.currency.dto.MerchantRequestDTO;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paysystems.dto.MerchantEditListDTO;
import kz.capitalpay.server.paysystems.service.MerchantPaysystemService;
import kz.capitalpay.server.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api/v1/auth/paysystem", produces = "application/json;charset=UTF-8")
public class MerchantPaysystemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantPaysystemController.class);

    private final MerchantPaysystemService merchantPaysystemService;
    private final ValidationUtil validationUtil;

    public MerchantPaysystemController(MerchantPaysystemService merchantPaysystemService, ValidationUtil validationUtil) {
        this.merchantPaysystemService = merchantPaysystemService;
        this.validationUtil = validationUtil;
    }

    @PostMapping("/merchant/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO merchantList(@RequestBody MerchantRequestDTO dto) {
        LOGGER.info("Merchant List");
        LOGGER.info("MerchantRequestDTO: {}", dto);
        return merchantPaysystemService.findAll(dto);
    }

    @PostMapping("/merchant/edit")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO merchantEditList(@Valid @RequestBody MerchantEditListDTO request, Principal principal) {
        LOGGER.info("Merchant Edit List");
        return merchantPaysystemService.editList(principal, request);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
