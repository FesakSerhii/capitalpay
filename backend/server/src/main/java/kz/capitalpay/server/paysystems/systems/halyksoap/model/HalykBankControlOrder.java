package kz.capitalpay.server.paysystems.systems.halyksoap.model;

import javax.persistence.*;

@Entity
public class HalykBankControlOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String amount;
    private String merchantCertId;
    private String currencyCode;
    private String merchantId;
    @Column(columnDefinition = "text")
    private String merchantSign;
    private String bankName;
    @Column(columnDefinition = "text")
    private String bankSign;
    private String reference;
    private String responseCode;
    private String approvalCode;
    private String command;
    private String reason;
    private String remainingAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMerchantCertId() {
        return merchantCertId;
    }

    public void setMerchantCertId(String merchantCertId) {
        this.merchantCertId = merchantCertId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantSign() {
        return merchantSign;
    }

    public void setMerchantSign(String merchantSign) {
        this.merchantSign = merchantSign;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankSign() {
        return bankSign;
    }

    public void setBankSign(String bankSign) {
        this.bankSign = bankSign;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(String remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
}
