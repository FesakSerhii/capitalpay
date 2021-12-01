package kz.capitalpay.server.login.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.TwoFactorAuthDTO;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.login.service.TwoFactorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth/twofactorauth")
public class TwoFactorAuthController {

    Logger logger = LoggerFactory.getLogger(TwoFactorAuthController.class);

    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    TwoFactorService twoFactorService;

    @PostMapping("/set")
    ResultDTO twoFactorAuth(@RequestBody TwoFactorAuthDTO request, Principal principal) {
        logger.info(gson.toJson(request));
        return  applicationUserService.twoFactorAuth(principal,request);
    }

    @PostMapping("/available")
    boolean isAvailableTwoFactorAuth(Principal principal) {
        return twoFactorService.isAvailableTwoFactorAuth(principal);
    }
}
