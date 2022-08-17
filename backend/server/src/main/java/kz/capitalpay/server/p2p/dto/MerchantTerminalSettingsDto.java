package kz.capitalpay.server.p2p.dto;

import javax.validation.constraints.NotNull;

public class MerchantTerminalSettingsDto {
    @NotNull
    private Long merchantId;
    @NotNull
    private Long terminalId;

    public MerchantTerminalSettingsDto(Long merchantId, Long terminalId) {
        this.merchantId = merchantId;
        this.terminalId = terminalId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }
}
