package kz.capitalpay.server.usercard.dto;

import javax.validation.constraints.NotNull;

public class RegisterMerchantCardWithBankDto {
    @NotNull
    private Long merchantId;
    private Long cashBoxId;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getCashBoxId() {
        return cashBoxId;
    }

    public void setCashBoxId(Long cashBoxId) {
        this.cashBoxId = cashBoxId;
    }
}
