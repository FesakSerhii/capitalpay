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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.math.BigDecimal;

@Controller
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
               @RequestParam BigDecimal totalamount,
               @RequestParam String currency,
               @RequestParam(defaultValue = "") String description,
               @RequestParam(required = false) String param,
               HttpServletRequest httpRequest,
               ModelMap modelMap
    ) {


        ResultDTO resultDTO = simpleService.createPayment(httpRequest, cashboxid, billid, totalamount, currency, description, param);
        if (resultDTO.isResult() && resultDTO.getData() instanceof Payment) {
            Payment payment = (Payment) resultDTO.getData();

            modelMap.addAttribute("paymentid", payment.getGuid());
            modelMap.addAttribute("billid", payment.getBillId());
            modelMap.addAttribute("totalamount", payment.getTotalAmount());
            modelMap.addAttribute("currency", payment.getCurrency());
            modelMap.addAttribute("description", payment.getDescription());

            return "paysystems/cardpay";
        } else {
            modelMap.addAttribute("message", resultDTO.getData());
            return "paysystems/error";
        }
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

    // Signature: SHA256(cashboxid + billid + secret)

    @PostMapping("/order")
    @ResponseBody
    ResultDTO getPaymentInfo(@RequestParam Long cashboxid, @RequestParam String billid, @RequestParam String signature,
                             HttpServletRequest httpRequest,
                             ModelMap modelMap) {


        ResultDTO resultDTO = simpleService.getPaymentInfo(httpRequest, cashboxid, billid, signature);
        if (!resultDTO.isResult()) {
            logger.error(gson.toJson(resultDTO));
        }
        return resultDTO;
    }

}
