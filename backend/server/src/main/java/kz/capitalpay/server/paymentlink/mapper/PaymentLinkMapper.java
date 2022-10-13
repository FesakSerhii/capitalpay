package kz.capitalpay.server.paymentlink.mapper;

import kz.capitalpay.server.paymentlink.dto.PaymentLinkResponseDto;
import kz.capitalpay.server.paymentlink.model.PaymentLink;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentLinkMapper {

    public PaymentLinkResponseDto toPaymentLinkResponseDto(PaymentLink paymentLink) {
        PaymentLinkResponseDto dto = new PaymentLinkResponseDto();
        dto.setBillId(paymentLink.getBillId());
        dto.setDescription(paymentLink.getDescription());
        dto.setSuccessfulPayment(paymentLink.isSuccessfulPayment());
        dto.setEmailTitle(paymentLink.getEmailTitle());
        dto.setEmailText(paymentLink.getEmailText());
        dto.setCashBoxId(paymentLink.getCashBoxId());
        dto.setGuid(paymentLink.getGuid());
        dto.setMerchantName(paymentLink.getMerchantName());
        dto.setMerchantEmail(paymentLink.getMerchantEmail());
        dto.setValidTill(paymentLink.getValidTill());
        dto.setTotalAmount(paymentLink.getTotalAmount());
        dto.setPayerEmail(paymentLink.getPayerEmail());
        dto.setValid(!paymentLink.isSuccessfulPayment() || paymentLink.getValidTill().isAfter(LocalDateTime.now()));
        return dto;
    }
}
