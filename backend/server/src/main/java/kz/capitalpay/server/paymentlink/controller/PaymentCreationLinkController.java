package kz.capitalpay.server.paymentlink.controller;

import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paymentlink.dto.CreateLinkWithLinkDto;
import kz.capitalpay.server.paymentlink.dto.CreatePaymentCreationLinkDto;
import kz.capitalpay.server.paymentlink.model.EditPaymentCreationLinkDto;
import kz.capitalpay.server.paymentlink.service.PaymentLinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import static kz.capitalpay.server.login.service.ApplicationRoleService.MERCHANT;

@RestController
public class PaymentCreationLinkController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentCreationLinkController.class);
    private final PaymentLinkService paymentLinkService;

    public PaymentCreationLinkController(PaymentLinkService paymentLinkService) {
        this.paymentLinkService = paymentLinkService;
    }

    @RolesAllowed({MERCHANT})
    @PostMapping("/api/v1/auth/merchant-link/create")
    public ResultDTO createMerchantLink(@Valid @RequestBody CreatePaymentCreationLinkDto dto) {
        return paymentLinkService.createPaymentCreationLink(dto);
    }

    @RolesAllowed({MERCHANT})
    @PostMapping("/api/v1/auth/merchant-link/edit")
    public ResultDTO editMerchantLink(@Valid @RequestBody EditPaymentCreationLinkDto dto) {
        return paymentLinkService.editPaymentCreationLink(dto);
    }

    @RolesAllowed({MERCHANT})
    @DeleteMapping("/api/v1/auth/merchant-link/delete/{cashBoxId}")
    public ResultDTO deleteMerchantLink(@PathVariable Long cashBoxId) {
        return paymentLinkService.deletePaymentCreationLink(cashBoxId);
    }

    @RolesAllowed({MERCHANT})
    @GetMapping("/api/v1/auth/merchant-link/get/{cashBoxId}")
    public ResultDTO getMerchantLink(@PathVariable Long cashBoxId) {
        return paymentLinkService.getPaymentCreationLink(cashBoxId);
    }

    @PostMapping("/api/v1/payment-link/create-by-link")
    public ResultDTO createPaymentLink(@Valid @RequestBody CreateLinkWithLinkDto dto) {
        return paymentLinkService.createPaymentLink(dto);
    }

    @GetMapping("/api/v1/merchant-link/get-public-info/{id}")
    public ResultDTO getMerchantLinkPublicInfo(@PathVariable String id) {
        return paymentLinkService.getPaymentCreationLinkPublicInfo(id);
    }


}
