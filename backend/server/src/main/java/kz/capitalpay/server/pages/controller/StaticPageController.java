package kz.capitalpay.server.pages.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.pages.dto.DeleteDTO;
import kz.capitalpay.server.pages.dto.SavePageDTO;
import kz.capitalpay.server.pages.dto.ShowListDTO;
import kz.capitalpay.server.pages.dto.ShowOnePageDTO;
import kz.capitalpay.server.pages.service.StaticPageService;
import kz.capitalpay.server.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api/v1/auth/staticpage", produces = "application/json;charset=UTF-8")
public class StaticPageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticPageController.class);

    private final StaticPageService staticPageService;
    private final ValidationUtil validationUtil;

    public StaticPageController(StaticPageService staticPageService, ValidationUtil validationUtil) {
        this.staticPageService = staticPageService;
        this.validationUtil = validationUtil;
    }


    @PostMapping("/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO showAll(@Valid @RequestBody ShowListDTO request) {
        LOGGER.info("Static Page list");
        return staticPageService.showAll(request);
    }

    @PostMapping("/one")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO showOne(@Valid @RequestBody ShowOnePageDTO request, Principal principal) {
        LOGGER.info("Show One Page");
        return staticPageService.showOne(principal, request);
    }


    @PostMapping("/save")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO save(@Valid @RequestBody SavePageDTO request, Principal principal) {
        LOGGER.info("Show One Page");
        return staticPageService.save(principal, request);
    }

    @PostMapping("/delete")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO delete(@Valid @RequestBody DeleteDTO request, Principal principal) {
        LOGGER.info("Delete Page");
        return staticPageService.delete(principal, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
