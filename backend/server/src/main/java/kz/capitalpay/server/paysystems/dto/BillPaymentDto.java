package kz.capitalpay.server.paysystems.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BillPaymentDto {
    private String statusBill;
    private String merchantName;
    private String webSiteMerchant;
    private String orderId;
    private String dateTransaction;
    private String typeTransaction;
    private String paySystemName;
    private String numberTransaction;
    private String cardHolderName;
    private String cardNumber;
    private String purposePayment;
    private String amountPayment;
    private String amountFee;
    private String totalAmount;

    public String getStatusBill() {
        return statusBill;
    }

    public void setStatusBill(String statusBill) {
        this.statusBill = statusBill;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getWebSiteMerchant() {
        return webSiteMerchant;
    }

    public void setWebSiteMerchant(String webSiteMerchant) {
        this.webSiteMerchant = webSiteMerchant;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    public String getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(int typeTransaction) {
        this.typeTransaction = getTypeTransactionById(typeTransaction);
    }

    public String getPaySystemName() {
        return paySystemName;
    }

    public void setPaySystemName(String customerCardNumber) {
        this.paySystemName = checkPaySystem(customerCardNumber);
    }

    public String getNumberTransaction() {
        return numberTransaction;
    }

    public void setNumberTransaction(String numberTransaction) {
        this.numberTransaction = numberTransaction.replaceFirst("0*", "");
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = maskCardNumber(cardNumber);
    }

    public String getPurposePayment() {
        return purposePayment;
    }

    public void setPurposePayment(String purposePayment) {
        this.purposePayment = purposePayment;
    }

    public String getAmountPayment() {
        return amountPayment;
    }

    public void setAmountPayment(String amountPayment, String currency) {
        this.amountPayment = amountPayment + " " + currency;
    }

    public String getAmountFee() {
        return amountFee;
    }

    public void setAmountFee(String amountFee, String currency) {
        this.amountFee = amountFee + " " + currency;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount, String currency) {
        this.totalAmount = totalAmount + " " + currency;
    }

    private String getTypeTransactionById(int transactionType) {
        return transactionType == 1 ? "Retail" : "Unknown type";
    }

    private String maskCardNumber(String cardNumber) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cardNumber.length(); i++) {
            if (i < 4 || i > cardNumber.length() - 5) {
                builder.append(cardNumber.charAt(i));
            } else {
                builder.append("X");
            }
            if(i == 3 || i == 7 || i == 11) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    private static String checkPaySystem(String cardNumber) {
        if(cardNumber.length() == 18 && cardNumber.startsWith("3") || cardNumber.startsWith("5")
                || cardNumber.startsWith("6")) {
            return "Maestro";
        }
        if(cardNumber.length() == 16) {
            switch (cardNumber.charAt(0)) {
                case '2':
                    return "МИР";
                case '3':
                    return "American Express";
                case '4':
                    return "Visa";
                case '5':
                    return "Mastercard";
                default:
                    return "Unknown pay system";
            }
        }
        return "Unknown pay system";
    }
}
