package kz.capitalpay.server.usercard.dto;

public class CheckCardValidityResponseDto {

    private boolean valid;
    private Long cardId;

    public CheckCardValidityResponseDto(boolean valid, Long cardId) {
        this.valid = valid;
        this.cardId = cardId;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
