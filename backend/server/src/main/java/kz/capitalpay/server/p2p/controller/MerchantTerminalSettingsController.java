package kz.capitalpay.server.p2p.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.dto.IdDto;
import kz.capitalpay.server.p2p.dto.MerchantTerminalSettingsDto;
import kz.capitalpay.server.p2p.service.P2pSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;

@RestController
@RequestMapping("/api/v1/auth/merchant-terminal-settings")
//@RequestMapping("/api/merchant-terminal-settings")
public class MerchantTerminalSettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantTerminalSettingsController.class);
    private final P2pSettingsService p2pSettingsService;

    public MerchantTerminalSettingsController(P2pSettingsService p2pSettingsService) {
        this.p2pSettingsService = p2pSettingsService;
    }

    @RolesAllowed({ADMIN})
    @PostMapping("/set")
    public ResultDTO setMerchantTerminalSettings(@RequestBody @Valid MerchantTerminalSettingsDto dto) {
        return p2pSettingsService.setMerchantTerminalSettings(dto);
    }

    @RolesAllowed({ADMIN})
    @PostMapping("/get")
    public ResultDTO getMerchantTerminalSettings(@RequestBody @Valid IdDto dto) {
        return p2pSettingsService.getMerchantTerminalSettings(dto.getId());
    }
}
