package kz.capitalpay.server.help.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.help.dto.ChangeStatusSupportRequestDTO;
import kz.capitalpay.server.help.dto.OneRequestDTO;
import kz.capitalpay.server.help.dto.SendSupportAnswerDTO;
import kz.capitalpay.server.help.dto.SupportRequestDTO;
import kz.capitalpay.server.help.service.SupportService;
import kz.capitalpay.server.login.service.ApplicationUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@RestController
@RequestMapping(value = "/api/v1/auth/support", produces = "application/json;charset=UTF-8")
public class SupportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupportController.class);

    @Autowired
    Gson gson;

    @Autowired
    SupportService supportService;

    @Autowired
    ApplicationUserService applicationUserService;


    @PostMapping("/request")
    @RolesAllowed({MERCHANT})
    ResultDTO supportRequest(Principal principal, @RequestBody SupportRequestDTO request) {
        LOGGER.info(gson.toJson(request));
        return supportService.supportRequest(principal, request);
    }

    @PostMapping("/list")
    @RolesAllowed({OPERATOR, ADMIN})
    ResultDTO requestList() {
        LOGGER.info("Support request list");
        return supportService.requestList();
    }

    @PostMapping("/list-by-merchantid")
    @RolesAllowed({OPERATOR, ADMIN})
    ResultDTO getRequestsByMerchantId(HttpServletRequest request) {
        LOGGER.info("Support request list");
        Long merchantId = applicationUserService.getMerchantIdFromToken(request);
        return supportService.getRequestsByMerchantId(merchantId);
    }


    @PostMapping("/one")
    @RolesAllowed({OPERATOR, ADMIN})
    ResultDTO oneRequest(@RequestBody OneRequestDTO request) {
        LOGGER.info("Support request list");
        return supportService.oneRequest(request);
    }


    @PostMapping("/status")
    @RolesAllowed({OPERATOR, ADMIN})
    ResultDTO changeStatus(Principal principal, @RequestBody ChangeStatusSupportRequestDTO request) {
        LOGGER.info(gson.toJson(request));
        return supportService.changeStatus(principal, request);
    }


    @PostMapping("/answer")
    @RolesAllowed({OPERATOR, ADMIN})
    ResultDTO sendAnswer(Principal principal, @RequestBody SendSupportAnswerDTO request) {
        LOGGER.info(gson.toJson(request));
        return supportService.sendSupportAnswer(principal, request);
    }
}
