package kz.capitalpay.server.login.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.login.service.UserEmailService;
import kz.capitalpay.server.login.service.UserPhoneService;
import kz.capitalpay.server.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/signup")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    UserEmailService userEmailService;

    @Autowired
    UserPhoneService userPhoneService;

    @Autowired
    Gson gson;

    @Autowired
    ValidationUtil validationUtil;

    /*

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
*/

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }


}
