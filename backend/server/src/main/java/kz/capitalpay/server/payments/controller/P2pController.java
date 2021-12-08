package kz.capitalpay.server.payments.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.payments.dto.SendP2pToClientDto;
import kz.capitalpay.server.payments.service.P2pService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/p2p")
public class P2pController {


    private static final Logger LOGGER = LoggerFactory.getLogger(P2pController.class);
    private final P2pService p2pService;

    public P2pController(P2pService p2pService) {
        this.p2pService = p2pService;
    }


    @PostMapping("/send-p2p-to-client")
    public ResultDTO sendP2pToClient(@RequestBody @Valid SendP2pToClientDto dto,
                                     HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");
        return p2pService.sendP2pToClient(dto, userAgent, ipAddress);
    }
}
