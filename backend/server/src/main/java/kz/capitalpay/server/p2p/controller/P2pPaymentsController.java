package kz.capitalpay.server.p2p.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.p2p.dto.SendAnonymousP2pToMerchantDto;
import kz.capitalpay.server.p2p.dto.SendP2pToClientDto;
import kz.capitalpay.server.p2p.service.P2pPaymentService;
import kz.capitalpay.server.p2p.service.P2pService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.halyksoap.model.HalykOrder;
import kz.capitalpay.server.paysystems.systems.halyksoap.service.HalykSoapService;
import kz.capitalpay.server.simple.service.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.REDIRECT_FAILED_URL;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.REDIRECT_SUCCESS_URL;

@RestController
@RequestMapping("/api/v1/p2p")
public class P2pPaymentsController {


    private static final Logger LOGGER = LoggerFactory.getLogger(P2pPaymentsController.class);
    private final P2pService p2pService;
    private final P2pPaymentService p2pPaymentService;
    private final HalykSoapService halykSoapService;
    private final Gson gson;
    private final CashboxSettingsService cashboxSettingsService;

    @Value("${payment.p2p.redirect.url}")
    private String paymentRedirectUrl;

    @Value("${kkbsign.send.order.action.link}")
    String actionLink;

    public P2pPaymentsController(P2pService p2pService, P2pPaymentService p2pPaymentService, HalykSoapService halykSoapService, Gson gson, CashboxSettingsService cashboxSettingsService) {
        this.p2pService = p2pService;
        this.p2pPaymentService = p2pPaymentService;
        this.halykSoapService = halykSoapService;
        this.gson = gson;
        this.cashboxSettingsService = cashboxSettingsService;
    }


    @PostMapping("/send-p2p-to-client")
    public RedirectView sendP2pToClient(@RequestParam @NotNull(message = "clientCardId must not be null") String clientCardToken,
                                        @RequestParam @NotNull(message = "merchantId must not be null") Long merchantId,
                                        @RequestParam @NotNull(message = "acceptedSum must not be null") BigDecimal acceptedSum,
                                        @RequestParam @NotNull(message = "cashBoxId must not be null") Long cashBoxId,
                                        @RequestParam @NotBlank(message = "sign must not be blank") String signature,
                                        HttpServletRequest httpRequest,
                                        RedirectAttributes redirectAttributes) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");
        SendP2pToClientDto dto = new SendP2pToClientDto(clientCardToken, merchantId, acceptedSum, cashBoxId, signature);
        return p2pService.sendP2pToClient(dto, userAgent, ipAddress, redirectAttributes);
    }

    @PostMapping("/send-p2p-to-merchant")
    public RedirectView sendP2pToMerchant(@RequestParam @NotNull(message = "clientCardId must not be null") String clientCardToken,
                                          @RequestParam @NotNull(message = "merchantId must not be null") Long merchantId,
                                          @RequestParam @NotNull(message = "acceptedSum must not be null") BigDecimal acceptedSum,
                                          @RequestParam @NotNull(message = "cashBoxId must not be null") Long cashBoxId,
                                          @RequestParam @NotBlank(message = "sign must not be blank") String signature,
                                          HttpServletRequest httpRequest,
                                          RedirectAttributes redirectAttributes) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        String userAgent = httpRequest.getHeader("User-Agent");
        SendP2pToClientDto dto = new SendP2pToClientDto(clientCardToken, merchantId, acceptedSum, cashBoxId, signature);
        return p2pService.sendP2pToMerchant(dto, userAgent, ipAddress, redirectAttributes);
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
        if (resultDTO.isResult() && resultDTO.getData() instanceof Payment) {
            Payment payment = (Payment) resultDTO.getData();
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
    public RedirectView listener(@RequestParam String PaRes,
                                 @RequestParam String MD,
                                 @RequestParam(required = false) String TermUrl) {
        LOGGER.info("PaRes: {}", PaRes);
        LOGGER.info("MD: {}", MD);
        LOGGER.info("TermUrl: {}", TermUrl);

        HalykOrder order = halykSoapService.getHalykOrderByMd(MD);
        Payment payment = halykSoapService.getPaymentByMd(MD);

        Map<String, String> resultUrls = cashboxSettingsService.getMerchantResultUrls(payment.getCashboxId());
        LOGGER.info(order.getSessionid());
        LOGGER.info(gson.toJson(payment));

        payment = halykSoapService.paymentOrderAcs(MD, PaRes, order.getSessionid(), order.getRequestType());
        if (Objects.isNull(payment)) {
            return new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
        }

        return payment.getStatus().equals(SimpleService.SUCCESS)
                ? new RedirectView(resultUrls.get(REDIRECT_SUCCESS_URL))
                : new RedirectView(resultUrls.get(REDIRECT_FAILED_URL));
    }
}
