package kz.capitalpay.server.paymentlink.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paymentlink.dto.CreatePaymentLinkDto;
import kz.capitalpay.server.paymentlink.service.PaymentLinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth/payment-link")
//@RequestMapping("/api/payment-link")
public class PaymentLinkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentLinkController.class);

    private final PaymentLinkService paymentLinkService;

    public PaymentLinkController(PaymentLinkService paymentLinkService) {
        this.paymentLinkService = paymentLinkService;
    }

    @PostMapping("/create")
    public ResultDTO createPaymentLink(@Valid @RequestBody CreatePaymentLinkDto dto) {
        return paymentLinkService.createPaymentLink(dto);
    }

    @GetMapping("/list")
    public ResultDTO getPaymentLinks(Principal principal) {
        return paymentLinkService.getList(principal);
    }

    @PostMapping("/renew/{linkId}")
    public ResultDTO renewPaymentLink(@PathVariable String linkId) {
        return paymentLinkService.renewPaymentLink(linkId);
    }
}
