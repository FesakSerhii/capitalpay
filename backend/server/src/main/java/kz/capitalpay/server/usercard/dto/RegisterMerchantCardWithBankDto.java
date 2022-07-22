package kz.capitalpay.server.usercard.dto;

import javax.validation.constraints.NotNull;

public class RegisterMerchantCardWithBankDto {
    @NotNull
    private Long merchantId;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
}
