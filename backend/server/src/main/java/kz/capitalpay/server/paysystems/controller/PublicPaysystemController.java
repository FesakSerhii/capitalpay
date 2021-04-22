package kz.capitalpay.server.paysystems.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paysystems.dto.PaymentRequestDTO;
import kz.capitalpay.server.paysystems.service.PaysystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping(value = "/public/api/paysystem", produces = "application/json;charset=UTF-8")
public class PublicPaysystemController {

    Logger logger = LoggerFactory.getLogger(PublicPaysystemController.class);

    @Autowired
    Gson gson;

    @Autowired
    PaysystemService paysystemService;

    @PostMapping("/system/buttonlist")
    ResultDTO paysystemButtonList(@RequestBody PaymentRequestDTO request) {
        logger.info("Paysystem Button List");
        return paysystemService.systemButtonList(request);
    }

}
