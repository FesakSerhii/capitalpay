package kz.capitalpay.server.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dictionary.dto.SaveTranslateRequestDTO;
import kz.capitalpay.server.dictionary.service.TranslateService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.dto.SendSmsDTO;
import kz.capitalpay.server.login.model.PendingPhone;
import kz.capitalpay.server.login.repository.PendingPhoneRepository;
import kz.capitalpay.server.paysystems.systems.testsystem.TestSystem;
import kz.capitalpay.server.service.SendEmailService;
import kz.capitalpay.server.service.SendSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TemporaryDevController {

    // TODO: Это временный контроллер для разработчиков.
    // В будущем нужно будет удалить

    Logger logger = LoggerFactory.getLogger(TemporaryDevController.class);

    @Autowired
    Gson gson;

    @Autowired
    TranslateService translateService;

    @Autowired
    PendingPhoneRepository pendingPhoneRepository;

    @Autowired
    SendSmsService sendSmsService;

    @PostMapping("/translate/save")
    ResultDTO saveTranslate(@Valid @RequestBody SaveTranslateRequestDTO request) {
        logger.info(gson.toJson(request));
        return translateService.save(request);
    }


//
//    @PostMapping("/test/test")
//    Boolean saveTranslate( @RequestBody TestTestDTO request) {
//        logger.info(gson.toJson(request));
//        return true;
//    }
//
// static    class TestTestDTO{
//        Long id;
//        BigDecimal a;
//        String b;
//
//        public Long getId() {
//            return id;
//        }
//
//        public void setId(Long id) {
//            this.id = id;
//        }
//
//        public BigDecimal getA() {
//            return a;
//        }
//
//        public void setA(BigDecimal a) {
//            this.a = a;
//        }
//
//        public String getB() {
//            return b;
//        }
//
//        public void setB(String b) {
//            this.b = b;
//        }
//    }

//    @PostMapping("/sms/test")
//    ResultDTO sendSms(@RequestBody SendSmsDTO request) {
//        logger.info(gson.toJson(request));
//        return sendSmsService.sendNewSms(request);
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
