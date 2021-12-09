package kz.capitalpay.server.usercard.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RegisterUserCardDto {

    @NotBlank(message = "cardNumber must not be blank")
    private String cardNumber;
    @NotBlank(message = "expireYear must not be blank")
    private String expireYear;
    @NotBlank(message = "expireMonth must not be blank")
    private String expireMonth;
    @NotBlank(message = "cvv2Code must not be blank")
    private String cvv2Code;
    @NotNull(message = "merchantId must not be null")
    private Long merchantId;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(String expireYear) {
        this.expireYear = expireYear;
    }

    public String getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth) {
        this.expireMonth = expireMonth;
    }

    public String getCvv2Code() {
        return cvv2Code;
    }

    public void setCvv2Code(String cvv2Code) {
        this.cvv2Code = cvv2Code;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
}
