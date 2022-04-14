package kz.capitalpay.server.p2p.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SendP2pToClientDto {

    @NotNull(message = "clientCardId must not be null")
    private String clientCardToken;
    @NotNull(message = "merchantId must not be null")
    private Long merchantId;
    @NotNull(message = "acceptedSum must not be null")
    private BigDecimal acceptedSum;
    @NotNull(message = "cashBoxId must not be null")
    private Long cashBoxId;
    @NotBlank(message = "sign must not be blank")
    private String signature;

    public SendP2pToClientDto(String clientCardToken, Long merchantId, BigDecimal acceptedSum, Long cashBoxId, String signature) {
        this.clientCardToken = clientCardToken;
        this.merchantId = merchantId;
        this.acceptedSum = acceptedSum;
        this.cashBoxId = cashBoxId;
        this.signature = signature;
    }

    public SendP2pToClientDto(Long merchantId, BigDecimal acceptedSum, Long cashBoxId) {
        this.merchantId = merchantId;
        this.acceptedSum = acceptedSum;
        this.cashBoxId = cashBoxId;
    }

    public String getClientCardToken() {
        return clientCardToken;
    }

    public void setClientCardToken(String clientCardToken) {
        this.clientCardToken = clientCardToken;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getAcceptedSum() {
        return acceptedSum;
    }

    public void setAcceptedSum(BigDecimal acceptedSum) {
        this.acceptedSum = acceptedSum;
    }

    public Long getCashBoxId() {
        return cashBoxId;
    }

    public void setCashBoxId(Long cashBoxId) {
        this.cashBoxId = cashBoxId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
