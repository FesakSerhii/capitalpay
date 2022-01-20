package kz.capitalpay.server.p2p.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.dto.SendAnonymousP2pToMerchantDto;
import kz.capitalpay.server.p2p.dto.SendP2pToClientDto;
import kz.capitalpay.server.p2p.model.P2pPayment;
import kz.capitalpay.server.p2p.service.P2pPaymentService;
import kz.capitalpay.server.p2p.service.P2pService;
import kz.capitalpay.server.paysystems.service.PaysystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/p2p")
public class P2pPaymentsController {


    private static final Logger LOGGER = LoggerFactory.getLogger(P2pPaymentsController.class);
    private final P2pService p2pService;
    private final PaysystemService paysystemService;
    private final P2pPaymentService p2pPaymentService;

    @Value("${payment.p2p.redirect.url}")
    private String paymentRedirectUrl;

    public P2pPaymentsController(P2pService p2pService, PaysystemService paysystemService, P2pPaymentService p2pPaymentService) {
        this.p2pService = p2pService;
        this.paysystemService = paysystemService;
        this.p2pPaymentService = p2pPaymentService;
    }


    @PostMapping("/send-p2p-to-client")
    public ResultDTO sendP2pToClient(@RequestBody @Valid SendP2pToClientDto dto,
                                     HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");
        return p2pService.sendP2pToClient(dto, userAgent, ipAddress);
    }

    @PostMapping("/send-p2p-to-merchant")
    public ResultDTO sendP2pToMerchant(@RequestBody @Valid SendP2pToClientDto dto,
                                       HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");
        return p2pService.sendP2pToMerchant(dto, userAgent, ipAddress);
    }

    @PostMapping("/send-anonymous-p2p-to-merchant")
    public void paymentCardPay(HttpServletRequest httpRequest,
                               HttpServletResponse httpResponse,
                               SendAnonymousP2pToMerchantDto dto) {

        paysystemService.paymentPayAndRedirect(
                httpRequest, httpResponse, dto.getPaymentId(), dto.getCardHolderName(), dto.getCvv(),
                dto.getMonth(), dto.getPan(), dto.getYear(), dto.getPhone(), dto.getEmail(), true);
    }

    @PostMapping("/anonymous-p2p-to-merchant")
    public RedirectView createAnonymousP2pPayment(@RequestParam Long cashBoxId,
                                                  @RequestParam BigDecimal totalAmount,
                                                  @RequestParam Long merchantId,
                                                  @RequestParam String currency,
                                                  @RequestParam String signature,
                                                  @RequestParam(required = false) String param,
                                                  HttpServletRequest httpRequest,
                                                  RedirectAttributes redirectAttributes
    ) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");
        ResultDTO resultDTO = p2pService.createAnonymousP2pPayment(userAgent, ipAddress, cashBoxId, merchantId,
                totalAmount, currency, param, signature);
        if (resultDTO.isResult() && resultDTO.getData() instanceof P2pPayment) {
            P2pPayment payment = (P2pPayment) resultDTO.getData();
            redirectAttributes.addAttribute("id", payment.getGuid());
        } else {
            redirectAttributes.addAttribute("error", resultDTO.getError());
            redirectAttributes.addAttribute("data", resultDTO.getData());
        }
        return new RedirectView(paymentRedirectUrl);
    }

    @PostMapping("/get-payment")
    public ResultDTO getP2pPaymentData(@RequestParam String id) {
        return p2pPaymentService.getP2pPaymentDataById(id);
    }
}
