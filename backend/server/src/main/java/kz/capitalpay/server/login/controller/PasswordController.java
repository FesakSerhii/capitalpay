package kz.capitalpay.server.login.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.NewPasswordRequestDTO;
import kz.capitalpay.server.login.dto.SignUpEmailRequestDTO;
import kz.capitalpay.server.login.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/password")
public class PasswordController {

    Logger logger = LoggerFactory.getLogger(PasswordController.class);

    @Autowired
    Gson gson;

    @Autowired
    PasswordService passwordService;

    @PostMapping("/new")
    ResultDTO newPassword(@Valid @RequestBody NewPasswordRequestDTO request, Principal principal) {
        logger.info(gson.toJson(request));
        return passwordService.setNewPassword(principal, request);
    }

}
