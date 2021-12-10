package kz.capitalpay.server.p2p.dto;

import javax.validation.constraints.NotNull;

public class P2pSettingsDto {
    @NotNull(message = "merchantId must not be null")
    private Boolean p2pAllowed;
    @NotNull(message = "merchantId must not be null")
    private Long merchantId;

    public Boolean getP2pAllowed() {
        return p2pAllowed;
    }

    public void setP2pAllowed(Boolean p2pAllowed) {
        this.p2pAllowed = p2pAllowed;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
}
