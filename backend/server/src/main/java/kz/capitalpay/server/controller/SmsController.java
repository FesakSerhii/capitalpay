package kz.capitalpay.server.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.dto.SendSmsDTO;
import kz.capitalpay.server.login.dto.SignUpEmailRequestDTO;
import kz.capitalpay.server.service.SendSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class SmsController {

    Logger logger = LoggerFactory.getLogger(SmsController.class);

    @Autowired
    Gson gson;

    @Autowired
    SendSmsService sendSmsService;


    @PostMapping("/calback/sms")
    void callbackUrl(
                     @RequestParam("") String phone,
                     @RequestParam("") String status,
                     @RequestParam("") String time,
                     @RequestParam("") String ts,
                     @RequestParam("") String id) {

        logger.info("{} {} {} {} {}",  phone, status, time, ts, id);

    }
}
