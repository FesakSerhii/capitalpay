package kz.capitalpay.server.p2p.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.dto.P2pSettingsDto;
import kz.capitalpay.server.p2p.service.P2pSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;

@RestController
@RequestMapping("/api/v1/auth/p2p-settings")
public class P2pSettingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(P2pPaymentsController.class);
    private final P2pSettingsService p2pSettingsService;

    public P2pSettingsController(P2pSettingsService p2pSettingsService) {
        this.p2pSettingsService = p2pSettingsService;
    }


    @PostMapping("/get")
    public ResultDTO getP2pSettings(@RequestParam Long merchantId) {
        return p2pSettingsService.getP2pSettingsByMerchantId(merchantId);
    }

    @PostMapping("/set")
    @RolesAllowed({ADMIN})
    public ResultDTO setP2pSettings(@RequestBody @Valid P2pSettingsDto dto) {
        return p2pSettingsService.setP2pSettings(dto);
    }
}
