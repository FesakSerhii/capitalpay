package kz.capitalpay.server.payments.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.payments.dto.OnePaymentDetailsRequestDTO;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.util.ValidationUtil;
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

import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@RestController
@RequestMapping(value = "/api/v1/auth/payments", produces = "application/json;charset=UTF-8")
public class PaymentController {

    Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    Gson gson;

    @Autowired
    PaymentService paymentService;

    @Autowired
    ValidationUtil validationUtil;

    @PostMapping("/list")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO paymentList(Principal principal) {
        logger.info("Payment List");
        return paymentService.paymentList(principal);
    }


    @PostMapping("/one")
    @RolesAllowed({ADMIN, OPERATOR, MERCHANT})
    ResultDTO onePayment(Principal principal, @Valid @RequestBody OnePaymentDetailsRequestDTO request) {
        logger.info("One payment");
        return paymentService.onePayment(principal, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
