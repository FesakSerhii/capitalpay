package kz.capitalpay.server.usercard.dto;

public class CheckCardValidityResponseDto {

    private boolean valid;
    private Long cardId;
    private String returnCode;

    public CheckCardValidityResponseDto(boolean valid, Long cardId, String returnCode) {
        this.valid = valid;
        this.cardId = cardId;
        this.returnCode = returnCode;
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

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }
}
