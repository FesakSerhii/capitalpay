package kz.capitalpay.server.pages.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dictionary.dto.GetDictionaryDTO;
import kz.capitalpay.server.dictionary.dto.SaveTranslateRequestDTO;
import kz.capitalpay.server.dictionary.service.TranslateService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.repository.PendingPhoneRepository;
import kz.capitalpay.server.service.SendSmsService;
import kz.capitalpay.server.util.ValidationUtil;
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
public class TranslateController {

    Logger logger = LoggerFactory.getLogger(TranslateController.class);

    @Autowired
    Gson gson;

    @Autowired
    TranslateService translateService;

    @Autowired
    PendingPhoneRepository pendingPhoneRepository;

    @Autowired
    SendSmsService sendSmsService;

    @Autowired
    ValidationUtil validationUtil;

    @PostMapping("/translate/save")
    ResultDTO saveTranslate(@Valid @RequestBody SaveTranslateRequestDTO request) {
        logger.info(gson.toJson(request));
        return translateService.save(request);
    }

    @PostMapping("/pagelist")
    ResultDTO pageList() {

        return translateService.pageList();
    }

    @PostMapping("/dictionary")
    ResultDTO getDictionary(@Valid @RequestBody GetDictionaryDTO request) {
        logger.info(gson.toJson(request));
        return translateService.getDictionary(request);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
