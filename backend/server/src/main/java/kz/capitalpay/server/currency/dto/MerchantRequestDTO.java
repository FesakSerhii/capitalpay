package kz.capitalpay.server.currency.dto;

import javax.validation.constraints.NotNull;

public class MerchantRequestDTO {

    @NotNull
    private Long merchantId;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    public String toString() {
        return "MerchantRequestDTO{" +
                "merchantId=" + merchantId +
                '}';
    }
}
