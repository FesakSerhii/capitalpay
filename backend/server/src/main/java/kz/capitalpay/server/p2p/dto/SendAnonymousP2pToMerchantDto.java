package kz.capitalpay.server.p2p.dto;

import javax.validation.constraints.NotNull;

public class SendAnonymousP2pToMerchantDto {
    @NotNull(message = "paymentId must not be null")
    private String paymentId;
    @NotNull(message = "cardHolderName must not be null")
    private String cardHolderName;
    @NotNull(message = "cvv must not be null")
    private String cvv;
    @NotNull(message = "month must not be null")
    private String month;
    @NotNull(message = "pan must not be null")
    private String pan;
    @NotNull(message = "year must not be null")
    private String year;
    @NotNull(message = "phone must not be null")
    private String phone;
    @NotNull(message = "email must not be null")
    private String email;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
