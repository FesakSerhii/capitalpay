package kz.capitalpay.server.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dictionary.dto.SaveTranslateRequestDTO;
import kz.capitalpay.server.dictionary.service.TranslateService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.PendingPhone;
import kz.capitalpay.server.login.repository.PendingPhoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

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

    @PostMapping("/translate/save")
    ResultDTO saveTranslate(@Valid @RequestBody SaveTranslateRequestDTO request) {
        logger.info(gson.toJson(request));
        return translateService.save(request);
    }

    @PostMapping("/pending/sms")
    ResultDTO pendingSmsList() {
        List<PendingPhone> pendingPhoneList = pendingPhoneRepository.findAll();
        return new ResultDTO(true, pendingPhoneList, 0);
    }

    @GetMapping("/api/test")
    String testMyData(Principal principal) {
        return gson.toJson(principal);
    }
}
