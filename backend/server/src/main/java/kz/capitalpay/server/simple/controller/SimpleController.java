package kz.capitalpay.server.simple.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paymentlink.model.PaymentLink;
import kz.capitalpay.server.paymentlink.service.PaymentLinkService;
import kz.capitalpay.server.simple.service.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping(value = "/payment/simple", produces = "application/json;charset=UTF-8")
public class SimpleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleController.class);

    @Autowired
    Gson gson;

    @Autowired
    SimpleService simpleService;

    @Autowired
    PaymentLinkService paymentLinkService;

//    @PostMapping("/pay")
//    String pay(@RequestParam Long cashboxid,
//               @RequestParam String billid,
//               @RequestParam BigDecimal totalamount,
//               @RequestParam String currency,
//               @RequestParam(defaultValue = "") String description,
//               @RequestParam(required = false) String param,
//               HttpServletRequest httpRequest,
//               ModelMap modelMap
//    ) {
//
//
//        ResultDTO resultDTO = simpleService.createPayment(httpRequest, cashboxid, billid, totalamount, currency, description, param);
//        if (resultDTO.isResult() && resultDTO.getData() instanceof Payment) {
//            Payment payment = (Payment) resultDTO.getData();
//            modelMap.addAttribute("paymentid", payment.getGuid());
//            modelMap.addAttribute("billid", payment.getBillId());
//            modelMap.addAttribute("totalamount", payment.getTotalAmount());
//            modelMap.addAttribute("currency", payment.getCurrency());
//            modelMap.addAttribute("description", payment.getDescription());
//            return "paysystems/cardpay";
//        } else {
//            modelMap.addAttribute("message", resultDTO.getData());
//            return "paysystems/error";
//        }
//        /*
//        if (result.isResult() && result.getData() != null && result.getData() instanceof Payment) {
//            Payment payment = (Payment) result.getData();
//            httpServletResponse.setHeader("Location",
//                    "https://api.capitalpay.kz/paysystems/page?paymentid=" + payment.getGuid());
//            httpServletResponse.setStatus(302);
//        } else {
//
//           httpServletResponse.setContentType(MediaType.APPLICATION_JSON.toString());
//           try {
//               httpServletResponse.getWriter().write(gson.toJson(result));
//           }catch (Exception e){
//               e.printStackTrace();
//           }
//        }
//
//         */
//    }

    @PostMapping("/pay")
    String pay(@RequestParam Long cashboxid,
               @RequestParam String billid,
               @RequestParam BigDecimal totalamount,
               @RequestParam String currency,
               @RequestParam(defaultValue = "") String description,
               @RequestParam(required = false) String param,
               HttpServletRequest httpRequest,
               ModelMap modelMap) {
        ResultDTO resultDTO = simpleService.createBankPayment(httpRequest, cashboxid, billid, totalamount,
                currency, description, param, null);
        if (resultDTO.isResult()) {
            Map<String, String> resultMap = (Map<String, String>) resultDTO.getData();
            modelMap.addAttribute("xml", resultMap.get("xml"));
            modelMap.addAttribute("backLink", resultMap.get("backLink"));
            modelMap.addAttribute("postLink", resultMap.get("postLink"));
            return "purchase";
        } else {
            modelMap.addAttribute("message", resultDTO.getData());
            return "paysystems/error";
        }
    }

    @GetMapping("/pay-with-link/{linkId}")
    String pay(@PathVariable String linkId,
               HttpServletRequest httpRequest,
               ModelMap modelMap) {
        PaymentLink paymentLink = paymentLinkService.findById(linkId);
        if (Objects.isNull(paymentLink) || !paymentLink.getValidTill().isAfter(LocalDateTime.now())) {
            modelMap.addAttribute("message", "Link is invalid!");
            return "paysystems/error";
        }
        ResultDTO resultDTO = simpleService.createBankPayment(httpRequest, paymentLink.getCashBoxId(),
                paymentLink.getBillId(), paymentLink.getTotalAmount(), paymentLink.getCurrency(),
                paymentLink.getDescription(), null, paymentLink.getGuid());
        if (resultDTO.isResult()) {
            paymentLink.setValidTill(LocalDateTime.now());
            Map<String, String> resultMap = (Map<String, String>) resultDTO.getData();
            modelMap.addAttribute("xml", resultMap.get("xml"));
            modelMap.addAttribute("backLink", resultMap.get("backLink"));
            modelMap.addAttribute("postLink", resultMap.get("postLink"));
            return "purchase";
        } else {
            modelMap.addAttribute("message", resultDTO.getData());
            return "paysystems/error";
        }
    }

    // Signature: SHA256(cashboxid + billid + secret)

    @PostMapping("/order")
    @ResponseBody
    ResultDTO getPaymentInfo(@RequestParam Long cashboxid, @RequestParam String billid, @RequestParam String signature,
                             HttpServletRequest httpRequest,
                             ModelMap modelMap) {
        ResultDTO resultDTO = simpleService.getPaymentInfo(httpRequest, cashboxid, billid, signature);
        if (!resultDTO.isResult()) {
            LOGGER.error(gson.toJson(resultDTO));
        }
        return resultDTO;
    }

}
