package com.card.holding.dto;

public class CardDataResponseDto {

    private String cardNumber;
    private String expireYear;
    private String expireMonth;
    private String cvv2Code;
    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
