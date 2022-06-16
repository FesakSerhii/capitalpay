package kz.capitalpay.server.pages.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dictionary.dto.GetDictionaryDTO;
import kz.capitalpay.server.dictionary.dto.SaveTranslateRequestDTO;
import kz.capitalpay.server.dictionary.service.TranslateService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class TranslateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslateController.class);

    private final Gson gson;
    private final TranslateService translateService;
    private final ValidationUtil validationUtil;

    public TranslateController(Gson gson, TranslateService translateService, ValidationUtil validationUtil) {
        this.gson = gson;
        this.translateService = translateService;
        this.validationUtil = validationUtil;
    }

    @PostMapping("/translate/save")
    ResultDTO saveTranslate(@Valid @RequestBody SaveTranslateRequestDTO request) {
        LOGGER.info(gson.toJson(request));
        return translateService.save(request);
    }

    @PostMapping("/pagelist")
    ResultDTO pageList() {

        return translateService.pageList();
    }

    @PostMapping("/dictionary")
    ResultDTO getDictionary(@Valid @RequestBody GetDictionaryDTO request) {
        LOGGER.info(gson.toJson(request));
        return translateService.getDictionary(request);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
