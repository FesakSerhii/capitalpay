package kz.capitalpay.server.simple.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.simple.dto.SimpleRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
                  HttpServletRequest httpRequest
    ) {
        SimpleRequestDTO request = new SimpleRequestDTO();

        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        request.setIpAddress(ipAddress);

        request.setUserAgent(httpRequest.getHeader("User-Agent"));

        request.setCashboxid(cashboxid);
        request.setBillid(billid);
        request.setTotalamount(totalamount);
        request.setCurrency(currency);
        request.setParam(param);

        logger.info(gson.toJson(request));

        return new ResultDTO(true, request, 0);
    }

}
