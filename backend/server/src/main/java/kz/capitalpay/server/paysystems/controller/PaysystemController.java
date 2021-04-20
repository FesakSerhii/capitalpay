package kz.capitalpay.server.paysystems.controller;


import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paysystems.service.PaysystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping(value = "/api/v1/paysystem", produces = "application/json;charset=UTF-8")
public class PaysystemController {

    Logger logger = LoggerFactory.getLogger(PaysystemController.class);

    @Autowired
    Gson gson;

    @Autowired
    PaysystemService paysystemService;

    @PostMapping("/system/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO paysystemList() {
        logger.info("Paysystem List");
        return paysystemService.paysystemList();
    }


}
