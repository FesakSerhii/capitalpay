package kz.capitalpay.server.pages.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.pages.dto.ShowOnePageDTO;
import kz.capitalpay.server.pages.service.StaticPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/staticpage", produces = "application/json;charset=UTF-8")
public class PublicStaticPage {

    private static final Logger logger = LoggerFactory.getLogger(PublicStaticPage.class);
    private final StaticPageService staticPageService;

    public PublicStaticPage(StaticPageService staticPageService) {
        this.staticPageService = staticPageService;
    }

    @PostMapping("/one")
    ResultDTO showPage(@Valid @RequestBody ShowOnePageDTO request) {
        logger.info("Show One Page");
        return staticPageService.showPage(request);
    }
}
