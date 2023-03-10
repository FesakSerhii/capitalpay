package kz.capitalpay.server.help.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.help.dto.FeedBackDTO;
import kz.capitalpay.server.help.service.FeedBackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/help")
public class FeedBackController {

    Logger logger = LoggerFactory.getLogger(FeedBackController.class);

    @Autowired
    FeedBackService feedBackService;

    @Autowired
    Gson gson;

    @PostMapping("/feedback")
    ResultDTO feedBack(@RequestBody FeedBackDTO request) {
        logger.info(gson.toJson(request));
        return feedBackService.feedBack(request);
    }

}
