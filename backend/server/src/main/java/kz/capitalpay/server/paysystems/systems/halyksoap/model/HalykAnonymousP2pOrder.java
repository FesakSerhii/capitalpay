package kz.capitalpay.server.paysystems.systems.halyksoap.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class HalykAnonymousP2pOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String amount;
    private String merchantCertId;
    private String merchantName;
    private String currencyCode;
    private String merchantId;
    private String merchantSign;
    private String bankName;
    private String bankSign;
    private String reference;
    private String responseCode;
    private String secure;
    private String cardBin;
    private String cardHash;
    private String approvalCode;
    private String timestamp;
    private String merchantMain;
    private String abonentIdTo;
    private String cardIdTo;
    private String orderTime;

    public String getAbonentIdTo() {
        return abonentIdTo;
    }

    public void setAbonentIdTo(String abonentIdTo) {
        this.abonentIdTo = abonentIdTo;
    }

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

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
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

    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    public String getCardBin() {
        return cardBin;
    }

    public void setCardBin(String cardBin) {
        this.cardBin = cardBin;
    }

    public String getCardHash() {
        return cardHash;
    }

    public void setCardHash(String cardHash) {
        this.cardHash = cardHash;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMerchantMain() {
        return merchantMain;
    }

    public void setMerchantMain(String merchantMain) {
        this.merchantMain = merchantMain;
    }

    public String getCardIdTo() {
        return cardIdTo;
    }

    public void setCardIdTo(String cardIdTo) {
        this.cardIdTo = cardIdTo;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}
