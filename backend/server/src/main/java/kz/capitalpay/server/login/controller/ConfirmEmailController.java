package kz.capitalpay.server.login.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.ConfirmCodeCheckRequestDTO;
import kz.capitalpay.server.login.service.UserEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
public class ConfirmEmailController {

    Logger logger = LoggerFactory.getLogger(ConfirmEmailController.class);

    @Autowired
    Gson gson;

    @Autowired
    UserEmailService userEmailService;



    @PostMapping("/confirm")
    ResultDTO checkConfirm(@Valid @RequestBody ConfirmCodeCheckRequestDTO request) {
        logger.info(gson.toJson(request));
        return  userEmailService.checkConfirm(request);
    }


}
