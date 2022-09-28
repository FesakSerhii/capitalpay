package kz.capitalpay.server.login.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.NewPasswordRequestDTO;
import kz.capitalpay.server.login.service.PasswordService;
import kz.capitalpay.server.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth/password")
public class PasswordController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordController.class);

    @Autowired
    Gson gson;

    @Autowired
    PasswordService passwordService;

    @Autowired
    ValidationUtil validationUtil;

    @PostMapping("/new")
    ResultDTO newPassword(@Valid @RequestBody NewPasswordRequestDTO request, Principal principal) {
        LOGGER.info(gson.toJson(request));
        return passwordService.setNewPassword(principal, request);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
