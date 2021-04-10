package kz.capitalpay.server.help.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.help.dto.SupportRequestDTO;
import kz.capitalpay.server.help.service.SupportService;
import kz.capitalpay.server.login.dto.ChangeRolesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;

import static kz.capitalpay.server.login.service.ApplicationRoleService.MERCHANT;

@RestController
@RequestMapping("/api/v1/support")
public class SupportController {

    Logger logger = LoggerFactory.getLogger(SupportController.class);

    @Autowired
    Gson gson;

    @Autowired
    SupportService supportService;


    @PostMapping("/request")
    @RolesAllowed({MERCHANT})
    ResultDTO supportRequest(Principal principal, @RequestBody SupportRequestDTO request) {
        logger.info(gson.toJson(request));
        return supportService.supportRequest(principal, request);
    }

}
