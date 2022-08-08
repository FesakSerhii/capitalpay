package kz.capitalpay.server.p2p.dto;

import javax.validation.constraints.NotNull;

public class P2pSettingsDto {
    @NotNull(message = "p2pAllowed must not be null")
    private boolean p2pAllowed;
    @NotNull(message = "merchantId must not be null")
    private Long merchantId;

    public P2pSettingsDto(boolean p2pAllowed, Long merchantId) {
        this.p2pAllowed = p2pAllowed;
        this.merchantId = merchantId;
    }

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
