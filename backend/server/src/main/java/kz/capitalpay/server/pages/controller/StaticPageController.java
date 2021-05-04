package kz.capitalpay.server.pages.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.pages.service.StaticPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping(value = "/api/v1/staticpage", produces = "application/json;charset=UTF-8")
public class StaticPageController {

    Logger logger = LoggerFactory.getLogger(StaticPageController.class);

    @Autowired
    Gson gson;

    @Autowired
    StaticPageService staticPageService;


    @PostMapping("/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO showAll( ) {
        logger.info("Static Page list");
        return staticPageService.showAll();
    }

}
