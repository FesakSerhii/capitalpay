package kz.capitalpay.server.payments.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SendP2pToClientDto {

    @NotNull(message = "clientCardId must not be null")
    private Long clientCardId;
    @NotNull(message = "merchantId must not be null")
    private Long merchantId;
    @NotNull(message = "acceptedSum must not be null")
    private BigDecimal acceptedSum;
    @NotNull(message = "cashBoxId must not be null")
    private Long cashBoxId;
    @NotBlank(message = "sign must not be blank")
    private String signature;

    public Long getClientCardId() {
        return clientCardId;
    }

    public void setClientCardId(Long clientCardId) {
        this.clientCardId = clientCardId;
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
