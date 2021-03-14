package kz.capitalpay.server.dictionary.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dictionary.dto.GetDictionaryDTO;
import kz.capitalpay.server.dictionary.dto.SaveTranslateRequestDTO;
import kz.capitalpay.server.dictionary.service.TranslateService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.SignUpEmailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/translate")
public class TranslateController {

    Logger logger = LoggerFactory.getLogger(TranslateController.class);

    @Autowired
    Gson gson;

    @Autowired
    TranslateService translateService;


    @PostMapping("/pagelist")
    ResultDTO pageList() {

        return translateService.pageList();
    }

    @PostMapping("/dictionary")
    ResultDTO getDictionary(@Valid @RequestBody GetDictionaryDTO request) {
        logger.info(gson.toJson(request));
        return translateService.getDictionary(request);
    }
}
