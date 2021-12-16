package kz.capitalpay.server.cashbox.dto;

import javax.validation.constraints.NotNull;

public class SetCashBoxP2pSettingsDto {

    @NotNull(message = "cashBoxId must not be null")
    private Long cashBoxId;
    @NotNull(message = "p2pAllowed must not be null")
    private boolean p2pAllowed;
    @NotNull(message = "useDefaultCard must not be null")
    private boolean useDefaultCard;

    public Long getCashBoxId() {
        return cashBoxId;
    }

    public void setCashBoxId(Long cashBoxId) {
        this.cashBoxId = cashBoxId;
    }

    public boolean isP2pAllowed() {
        return p2pAllowed;
    }

    public void setP2pAllowed(boolean p2pAllowed) {
        this.p2pAllowed = p2pAllowed;
    }

    public boolean isUseDefaultCard() {
        return useDefaultCard;
    }

    public void setUseDefaultCard(boolean useDefaultCard) {
        this.useDefaultCard = useDefaultCard;
    }
}
