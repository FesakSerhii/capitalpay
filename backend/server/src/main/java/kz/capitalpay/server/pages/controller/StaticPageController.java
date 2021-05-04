package kz.capitalpay.server.pages.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.merchantsettings.dto.MerchantKycDTO;
import kz.capitalpay.server.pages.dto.SavePageDTO;
import kz.capitalpay.server.pages.dto.ShowListDTO;
import kz.capitalpay.server.pages.dto.ShowOnePageDTO;
import kz.capitalpay.server.pages.service.StaticPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

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
    ResultDTO showAll(@Valid @RequestBody ShowListDTO request) {
        logger.info("Static Page list");
        return staticPageService.showAll(request);
    }

    @PostMapping("/one")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO showOne(@Valid @RequestBody ShowOnePageDTO request, Principal principal) {
        logger.info("Show One Page");
        return staticPageService.showOne(principal, request);
    }


    @PostMapping("/save")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO save(@Valid @RequestBody SavePageDTO request, Principal principal) {
        logger.info("Show One Page");
        return staticPageService.save(principal, request);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResultDTO(false, errors, -2);
    }
}
