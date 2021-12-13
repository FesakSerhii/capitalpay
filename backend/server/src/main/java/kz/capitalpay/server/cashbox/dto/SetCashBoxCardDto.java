package kz.capitalpay.server.cashbox.dto;

import javax.validation.constraints.NotNull;

public class SetCashBoxCardDto {

    @NotNull(message = "cashBoxId must not be null")
    private Long cashBoxId;
    @NotNull(message = "cardId must not be null")
    private Long cardId;
    @NotNull(message = "cardId must not be null")
    private Long merchantId;

    public Long getCashBoxId() {
        return cashBoxId;
    }

    public void setCashBoxId(Long cashBoxId) {
        this.cashBoxId = cashBoxId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
}
