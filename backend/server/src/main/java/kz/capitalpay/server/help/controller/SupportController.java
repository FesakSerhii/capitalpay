package kz.capitalpay.server.help.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.help.dto.ChangeStatusSupportRequestDTO;
import kz.capitalpay.server.help.dto.OneRequestDTO;
import kz.capitalpay.server.help.dto.SendSupportAnswerDTO;
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

import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@RestController
@RequestMapping(value = "/api/v1/support", produces = "application/json;charset=UTF-8")
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

    @PostMapping("/list")
    @RolesAllowed({OPERATOR, ADMIN})
    ResultDTO requestList() {
        logger.info("Support request list");
        return supportService.requestList();
    }


    @PostMapping("/one")
    @RolesAllowed({OPERATOR, ADMIN})
    ResultDTO oneRequest(@RequestBody OneRequestDTO request) {
        logger.info("Support request list");
        return supportService.oneRequest(request);
    }



    @PostMapping("/status")
    @RolesAllowed({OPERATOR, ADMIN})
    ResultDTO changeStatus(Principal principal, @RequestBody ChangeStatusSupportRequestDTO request) {
        logger.info(gson.toJson(request));
        return supportService.changeStatus(principal,request);
    }


    @PostMapping("/answer")
    @RolesAllowed({OPERATOR, ADMIN})
    ResultDTO sendAnswer(Principal principal, @RequestBody SendSupportAnswerDTO request) {
        logger.info(gson.toJson(request));
        return supportService.sendSupportAnswer(principal,request);
    }
}
