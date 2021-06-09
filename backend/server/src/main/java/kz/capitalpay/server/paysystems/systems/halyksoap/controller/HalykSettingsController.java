package kz.capitalpay.server.paysystems.systems.halyksoap.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.dto.HalykDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public ResultDTO setHalykSettings(@Valid @RequestBody HalykDTO request, Principal principal) {
        logger.info("halyk set");
        return halykSettingsService.setOrUpdateHalykSettings(principal, request);
    }

    @GetMapping("/view")
    @RolesAllowed({ADMIN, OPERATOR})
    public String viewHalykSettings(Principal principal, Model model) {
        logger.info(" halyk data " + halykSettingsService.getHalykSettings(principal).getData());
        model.addAttribute("halykSettings", halykSettingsService.getHalykSettings(principal).getData());
        return "halyksettings";
    }
}
