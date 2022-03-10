package kz.capitalpay.server.p2p.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.p2p.dto.SendAnonymousP2pToMerchantDto;
import kz.capitalpay.server.p2p.dto.SendP2pToClientDto;
import kz.capitalpay.server.p2p.model.P2pPayment;
import kz.capitalpay.server.p2p.service.P2pPaymentService;
import kz.capitalpay.server.p2p.service.P2pService;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/p2p")
public class P2pPaymentsController {


    private static final Logger LOGGER = LoggerFactory.getLogger(P2pPaymentsController.class);
    private final P2pService p2pService;
    private final P2pPaymentService p2pPaymentService;
    private final HalykSoapService halykSoapService;
    private final CashboxService cashboxService;
    private final Gson gson;

    @Value("${payment.p2p.redirect.url}")
    private String paymentRedirectUrl;

    @Value("${kkbsign.send.order.action.link}")
    String actionLink;

    public P2pPaymentsController(P2pService p2pService, P2pPaymentService p2pPaymentService, HalykSoapService halykSoapService, CashboxService cashboxService, Gson gson) {
        this.p2pService = p2pService;
        this.p2pPaymentService = p2pPaymentService;
        this.halykSoapService = halykSoapService;
        this.cashboxService = cashboxService;
        this.gson = gson;
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
    public ResultDTO paymentCardPay(HttpServletRequest httpRequest,
                                    @Valid @RequestBody SendAnonymousP2pToMerchantDto dto) {

        return p2pService.sendAnonymousP2pPayment(httpRequest, dto.getPaymentId(), dto.getCardHolderName(),
                dto.getCvv(), dto.getMonth(), dto.getPan(), dto.getYear());
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

    @PostMapping("/paysystem/listener")
    public RedirectView listener(@RequestParam(required = false) String PaReq,
                                 @RequestParam String MD,
                                 @RequestParam String TermUrl,
                                 RedirectAttributes redirectAttributes) {
        LOGGER.info("PaRes: {}", PaReq);
        LOGGER.info("MD: {}", MD);
        LOGGER.info("TermUrl: {}", TermUrl);


        String sessionid;
        Object payment;
        // TODO: Костыль ради песочницы
        if (actionLink.equals("https://testpay.kkb.kz")) {
            sessionid = halykSoapService.getSessionByPaRes(PaReq);
            payment = halykSoapService.getPaymentByPaRes(PaReq);
        } else {
            sessionid = halykSoapService.getSessionByMD(MD);
            payment = halykSoapService.getPaymentByMd(MD);
        }

        LOGGER.info(sessionid);
        LOGGER.info(gson.toJson(payment));

        payment = halykSoapService.paymentOrderAcs(MD, PaReq, sessionid, true);
        P2pPayment p2pPayment = (P2pPayment) payment;

        redirectAttributes.addAttribute("paymentId", p2pPayment);
        redirectAttributes.addAttribute("status", p2pPayment.getStatus());
        return new RedirectView(paymentRedirectUrl);
    }
}
