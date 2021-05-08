package kz.capitalpay.server.simple.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.simple.dto.SimpleRequestDTO;
import kz.capitalpay.server.simple.service.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;

@RestController
@RequestMapping(value = "/payment/simple", produces = "application/json;charset=UTF-8")
public class SimpleController {

    Logger logger = LoggerFactory.getLogger(SimpleController.class);

    @Autowired
    Gson gson;

    @Autowired
    SimpleService simpleService;

    @PostMapping("/pay")
    String pay(@RequestParam Long cashboxid,
                  @RequestParam String billid,
                  @RequestParam Long totalamount,
                  @RequestParam String currency,
                  @RequestParam(required = false) String param,
                  HttpServletRequest httpRequest,
                  HttpServletResponse httpServletResponse
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

        ResultDTO result = simpleService.newPayment(request);

        return "paysystems/cardpay";
        /*
        if (result.isResult() && result.getData() != null && result.getData() instanceof Payment) {
            Payment payment = (Payment) result.getData();
            httpServletResponse.setHeader("Location",
                    "https://api.capitalpay.kz/paysystems/page?paymentid=" + payment.getGuid());
            httpServletResponse.setStatus(302);
        } else {

           httpServletResponse.setContentType(MediaType.APPLICATION_JSON.toString());
           try {
               httpServletResponse.getWriter().write(gson.toJson(result));
           }catch (Exception e){
               e.printStackTrace();
           }
        }

         */
    }
}
