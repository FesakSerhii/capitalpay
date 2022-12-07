package kz.capitalpay.server.payments.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.payments.dto.OnePaymentDetailsRequestDTO;
import kz.capitalpay.server.payments.dto.PaymentFilterDto;
import kz.capitalpay.server.payments.dto.SearchTextDto;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@RestController
@RequestMapping(value = "/api/v1/auth/payments", produces = "application/json;charset=UTF-8")
//@RequestMapping(value = "/api/v1/payments", produces = "application/json;charset=UTF-8")
public class PaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final ValidationUtil validationUtil;

    public PaymentController(PaymentService paymentService, ValidationUtil validationUtil) {
        this.paymentService = paymentService;
        this.validationUtil = validationUtil;
    }

    @PostMapping("/list")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO paymentList(Principal principal, @Valid @RequestBody PaymentFilterDto filter) {
        LOGGER.info("Payment List");
        return paymentService.paymentList(principal, filter);
    }

//    @PostMapping("/list")
//    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
//    ResultDTO paymentList(Principal principal) {
//        LOGGER.info("Payment List");
//        return paymentService.paymentList(principal);
//    }


    @PostMapping("/one")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO onePayment(Principal principal, @Valid @RequestBody OnePaymentDetailsRequestDTO request) {
        LOGGER.info("One payment");
        return paymentService.onePayment(principal, request);
    }

    @PostMapping("/get/merchant-data")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO getMerchantNames(@RequestBody SearchTextDto dto) {
        LOGGER.info("getMerchantData()");
        return paymentService.getPaymentsSearchMerchantData(dto.getSearchText());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
