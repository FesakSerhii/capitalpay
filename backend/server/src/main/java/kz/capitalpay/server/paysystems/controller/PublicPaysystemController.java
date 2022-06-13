package kz.capitalpay.server.paysystems.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.paysystems.dto.BillPaymentDto;
import kz.capitalpay.server.paysystems.service.PaysystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/public/paysystem", produces = "application/json;charset=UTF-8")
public class PublicPaysystemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicPaysystemController.class);

    private final Gson gson;
    private final PaysystemService paysystemService;

    @Value("${halyk.soap.purchase.termurl}")
    String purchaseTermUrl;

    @Value("${halyk.soap.p2p.termurl}")
    private String p2pTermUrl;

    @Value("${remote.api.addres}")
    String apiAddress;

    public PublicPaysystemController(Gson gson, PaysystemService paysystemService) {
        this.gson = gson;
        this.paysystemService = paysystemService;
    }

    //
//    @PostMapping("/system/buttonlist")
//    ResultDTO paysystemButtonList(@RequestBody PaymentRequestDTO request) {
//        logger.info("Paysystem Button List");
//        return paysystemService.systemButtonList(request);
//    }
//

    @PostMapping("/pay")
    void paymentCardPay(HttpServletRequest httpRequest,
                        HttpServletResponse httpResponse,
                        @RequestParam String paymentid,
                        @RequestParam String cardHolderName,
                        @RequestParam String cvv,
                        @RequestParam String month,
                        @RequestParam String pan,
                        @RequestParam String year,
                        @RequestParam String phone,
                        @RequestParam String email

    ) {

        httpResponse = paysystemService.paymentPayAndRedirect(
                httpRequest, httpResponse,
                paymentid, cardHolderName, cvv, month, pan, year, phone, email);

    }


    @GetMapping("/secure/redirect")
    String secureRedirect(ModelMap modelMap, @RequestParam String acsUrl,
                          @RequestParam String MD,
                          @RequestParam String PaReq,
                          @RequestParam(required = false, defaultValue = "") String bill) {

        String termUrl = bill.trim().isEmpty() ? p2pTermUrl : purchaseTermUrl;
        modelMap.addAttribute("acsUrl", acsUrl);
        modelMap.addAttribute("MD", MD);
        modelMap.addAttribute("PaReq", PaReq);
        modelMap.addAttribute("TermUrl", termUrl);
        modelMap.addAttribute("bill", gson.fromJson(bill, BillPaymentDto.class));

        return "paysystems/secureredirect";

    }

    @GetMapping("/error")
    String error() {
        return "paysystems/error";
    }

    @GetMapping("/bill")
    String bill(@RequestParam String bill, Model model) {
        BillPaymentDto billPaymentDto = gson.fromJson(bill, BillPaymentDto.class);
        model.addAttribute("bill", billPaymentDto);
        if (!"ok".equalsIgnoreCase(billPaymentDto.getResultPayment())) {
            return "paysystems/errorbill";
        }
        return "paysystems/bill";
    }
}
