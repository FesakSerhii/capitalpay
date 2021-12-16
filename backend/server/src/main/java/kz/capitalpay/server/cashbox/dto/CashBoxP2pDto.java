package kz.capitalpay.server.cashbox.dto;

public class CashBoxP2pDto {

    private Long id;
    private Long merchantId;
    private Long cardId;
    private String cardNumber;
    private boolean p2pAllowed;
    private boolean useDefaultCard;

    public CashBoxP2pDto() {
    }

    public CashBoxP2pDto(Long id, Long merchantId, Long cardId, String cardNumber, boolean p2pAllowed, boolean useDefaultCard) {
        this.id = id;
        this.merchantId = merchantId;
        this.cardId = cardId;
        this.cardNumber = cardNumber;
        this.p2pAllowed = p2pAllowed;
        this.useDefaultCard = useDefaultCard;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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
