package kz.capitalpay.server.paymentlink.mapper;

import kz.capitalpay.server.paymentlink.dto.PaymentLinkResponseDto;
import kz.capitalpay.server.paymentlink.model.PaymentLink;
import kz.capitalpay.server.util.QrCodeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentLinkMapper {

    private final QrCodeUtil qrCodeUtil;

    @Value("${remote.api.addres}")
    private String apiAddress;

    public PaymentLinkMapper(QrCodeUtil qrCodeUtil) {
        this.qrCodeUtil = qrCodeUtil;
    }

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
        String link = apiAddress + "/payment/simple/pay-with-link/" + paymentLink.getGuid();
        dto.setLink(link);
        dto.setQrCode(qrCodeUtil.generateQrCode(link));
        return dto;
    }
}
