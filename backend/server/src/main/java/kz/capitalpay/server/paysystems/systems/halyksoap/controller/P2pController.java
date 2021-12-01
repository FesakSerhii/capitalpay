package kz.capitalpay.server.paysystems.systems.halyksoap.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("paysystem/halyk/p2p")
public class P2pController {

    private static final Logger LOGGER = LoggerFactory.getLogger(P2pController.class);
    private final HalykSoapService halykSoapService;

    public P2pController(HalykSoapService halykSoapService) {
        this.halykSoapService = halykSoapService;
    }

//    @PostMapping("/send-request")
//    public ResultDTO sendP2pRequest() {
//
//    }
}
