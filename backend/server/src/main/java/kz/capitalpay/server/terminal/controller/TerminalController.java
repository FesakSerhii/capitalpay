package kz.capitalpay.server.terminal.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.dto.IdDto;
import kz.capitalpay.server.terminal.dto.TerminalDto;
import kz.capitalpay.server.terminal.service.TerminalService;
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
@RequestMapping("/api/v1/auth/terminal")
//@RequestMapping("/api/v1/terminal")
public class TerminalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalController.class);
    private final TerminalService terminalService;

    public TerminalController(TerminalService terminalService) {
        this.terminalService = terminalService;
    }


    @RolesAllowed({ADMIN})
    @PostMapping("/create")
    public ResultDTO createTerminal(@RequestBody @Valid TerminalDto dto) {
        return terminalService.saveTerminal(dto);
    }

    @RolesAllowed({ADMIN})
    @PostMapping("/edit")
    public ResultDTO editTerminal(@RequestBody @Valid TerminalDto dto) {
        return terminalService.saveTerminal(dto);
    }

    @RolesAllowed({ADMIN})
    @PostMapping("/delete")
    public ResultDTO deleteTerminal(@RequestBody @Valid IdDto dto) {
        return terminalService.deleteTerminal(dto);
    }

    @RolesAllowed({ADMIN})
    @PostMapping("/get")
    public ResultDTO getTerminal(@RequestBody @Valid IdDto dto) {
        return terminalService.findTerminalById(dto.getId());
    }

    @RolesAllowed({ADMIN})
    @PostMapping("/all")
    public ResultDTO getAll() {
        return terminalService.findAll();
    }

    @RolesAllowed({ADMIN})
    @PostMapping("/free")
    public ResultDTO getFreeTerminals() {
        return terminalService.findFreeTerminals();
    }


}
