package kz.capitalpay.server.payments.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.payments.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;

import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@RestController
@RequestMapping(value = "/api/v1/payments", produces = "application/json;charset=UTF-8")
public class PaymentController {

    Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    Gson gson;

    @Autowired
    PaymentService paymentService;

    @PostMapping("/list")
    @RolesAllowed({ADMIN, OPERATOR,MERCHANT})
    ResultDTO paymentList(Principal principal) {
        logger.info("Payment List");
        return paymentService.paymentList(principal);
    }


}
