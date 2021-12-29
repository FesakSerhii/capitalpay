package kz.capitalpay.server.usercard.dto;

import javax.validation.constraints.NotNull;

public class DeleteUserCardDto {
    @NotNull(message = "cardId must not be null")
    private Long cardId;
    @NotNull(message = "merchantId must not be null")
    private Long merchantId;

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
