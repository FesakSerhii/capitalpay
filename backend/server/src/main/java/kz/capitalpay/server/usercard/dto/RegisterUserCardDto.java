package kz.capitalpay.server.usercard.dto;

import javax.validation.constraints.NotBlank;

public class RegisterUserCardDto {

    @NotBlank(message = "cardNumber must not be blank")
    private String cardNumber;
    @NotBlank(message = "expireYear must not be blank")
    private String expireYear;
    @NotBlank(message = "expireMonth must not be blank")
    private String expireMonth;
    @NotBlank(message = "cvv2Code must not be blank")
    private String cvv2Code;

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
}
