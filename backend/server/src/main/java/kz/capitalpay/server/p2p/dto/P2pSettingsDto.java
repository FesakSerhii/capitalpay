package kz.capitalpay.server.p2p.dto;

import javax.validation.constraints.NotNull;

public class P2pSettingsDto {
    private boolean p2pAllowed;
    @NotNull(message = "merchantId must not be null")
    private Long merchantId;

    public boolean isP2pAllowed() {
        return p2pAllowed;
    }

    public void setP2pAllowed(boolean p2pAllowed) {
        this.p2pAllowed = p2pAllowed;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
}
