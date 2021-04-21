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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                  @RequestParam(required = false) String param,
                  HttpServletRequest request
    ) {
        logger.info("cashboxid: {}", cashboxid);
        logger.info("billid: {}", billid);
        logger.info("totalamount: {}", totalamount);
        logger.info("currency: {}", currency);

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        String userAgent = request.getHeader("User-Agent");

        logger.info("ipAddress: {}", ipAddress);
        logger.info("userAgent: {}", userAgent);


        return new ResultDTO(true, "", 0);
    }

}
