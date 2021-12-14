package kz.capitalpay.server.usercard.dto;

import javax.validation.constraints.NotNull;

public class ChangeMerchantDefaultCardDto {

    @NotNull(message = "merchantId must not be null")
    private Long merchantId;
    @NotNull(message = "cardId must not be null")
    private Long cardId;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
