package kz.capitalpay.server.paysystems.systems.halyksoap.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HalykController {

    Logger logger = LoggerFactory.getLogger(HalykController.class);

    @Autowired
    Gson gson;

    @PostMapping("/paysystem/halyk/listener")
    @ResponseBody
    public String listener(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Request:");
        logger.info("3DS: {}", gson.toJson(request.getParameterMap()));

        return gson.toJson(request.getParameterMap());
    }


}
