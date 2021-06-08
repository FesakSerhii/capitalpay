package kz.capitalpay.server.paysystems.systems.halyksoap.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.HalykDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;
import static kz.capitalpay.server.login.service.ApplicationRoleService.OPERATOR;

@RestController
@RequestMapping("/api/v1/paysystems/halyk")
public class HalykSettingsController {
    private final Logger logger = LoggerFactory.getLogger(HalykSettingsController.class);

    @Autowired
    private HalykSettingsService halykSettingsService;

    @PostMapping("/set")
    @RolesAllowed({ADMIN, OPERATOR})
    ResultDTO setKyc(@Valid @RequestBody HalykDTO request, Principal principal) {
        logger.info("halyk set");
        return halykSettingsService.setHalykSettings(principal, request);
    }


    @PostMapping("/get")
    @RolesAllowed({ADMIN, OPERATOR})
    ResultDTO getKyc(@Valid @RequestBody HalykDTO request, Principal principal) {
        logger.info("halyk get");
        return halykSettingsService.getHalykSettings(principal, request);
    }
}
