package kz.capitalpay.server.simple.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/payment/simple", produces = "application/json;charset=UTF-8")
public class SimpleController {

    Logger logger = LoggerFactory.getLogger(SimpleController.class);

    @Autowired
    Gson gson;


    @PostMapping("/pay")
    ResultDTO pay(@RequestParam Long cashboxid,
                  @RequestParam String billid,
                  @RequestParam Long totalamount,
                  @RequestParam String currency,
                  @RequestParam(required = false) String param1,
                  @RequestParam(required = false) String param2,
                  @RequestParam(required = false) String param3,
                  @RequestParam(required = false) String param4,
                  @RequestParam(required = false) String param5,
                  @RequestParam(required = false) String param6,
                  @RequestParam(required = false) String param7,
                  @RequestParam(required = false) String param8,
                  @RequestParam(required = false) String param9,
                  @RequestParam(required = false) String param10,
                  @RequestParam(required = false) String param11,
                  @RequestParam(required = false) String param12,
                  @RequestParam(required = false) String param13,
                  @RequestParam(required = false) String param14,
                  @RequestParam(required = false) String param15
    ) {
        logger.info("cashboxid: {}", cashboxid);
        logger.info("billid: {}", billid);
        logger.info("totalamount: {}", totalamount);
        logger.info("currency: {}", currency);
        logger.info("param1: {}", param1);
        logger.info("param2: {}", param2);
        return new ResultDTO(true, "", 0);
    }

}
