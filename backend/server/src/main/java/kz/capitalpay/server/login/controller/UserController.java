package kz.capitalpay.server.login.controller;

import com.google.gson.Gson;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.ConfirmCodeCheckRequestDTO;
import kz.capitalpay.server.login.dto.SignUpEmailRequestDTO;
import kz.capitalpay.server.login.dto.SignUpPhoneRequestDTO;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.login.service.UserEmailService;
import kz.capitalpay.server.login.service.UserPhoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/signup")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    UserEmailService userEmailService;

    @Autowired
    UserPhoneService userPhoneService;

    @Autowired
    Gson gson;

    @PostMapping("/step1")
    ResultDTO step1(@Valid @RequestBody SignUpEmailRequestDTO request) {
        logger.info(gson.toJson(request));
        return userEmailService.setNewEmail(request);
    }

    @PostMapping("/step2")
    ResultDTO checkConfirm(@Valid @RequestBody ConfirmCodeCheckRequestDTO request) {
        logger.info(gson.toJson(request));
        return  userEmailService.checkConfirm(request);
    }

    @PostMapping("/step3")
    ResultDTO savePassword(@Valid @RequestBody SignUpPhoneRequestDTO request) {
        logger.info(gson.toJson(request));
        return  userPhoneService.savePassword(request);
    }


    @PostMapping("/step5")
    ResultDTO confirmPhone(@Valid @RequestBody ConfirmCodeCheckRequestDTO request) {
        logger.info(gson.toJson(request));
        return  userPhoneService.confirmPhone(request);
    }


//    @PostMapping("/step1")
//    void signUp(@RequestBody ApplicationUser applicationUser) {
//        logger.info(gson.toJson(applicationUser));
//        applicationUserService.signUp(applicationUser);
//    }


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
        return new ResultDTO(false, errors,-2);
    }
}
