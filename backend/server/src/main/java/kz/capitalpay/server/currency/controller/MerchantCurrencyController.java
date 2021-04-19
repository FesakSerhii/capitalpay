package kz.capitalpay.server.currency.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.currency.dto.MerchantRequestDTO;
import kz.capitalpay.server.currency.service.MerchantCurrencyService;
import kz.capitalpay.server.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping(value = "/api/v1/currency", produces = "application/json;charset=UTF-8")
public class MerchantCurrencyController {

    Logger logger = LoggerFactory.getLogger(MerchantCurrencyController.class);

    @Autowired
    Gson gson;

    @Autowired
    MerchantCurrencyService merchantCurrencyService;

    @PostMapping("/merchant/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO merchantList(@RequestBody MerchantRequestDTO request) {
        logger.info("System List");
        return merchantCurrencyService.findAll(request);
    }

}
