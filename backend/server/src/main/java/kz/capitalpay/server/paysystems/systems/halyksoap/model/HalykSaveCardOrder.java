package kz.capitalpay.server.paysystems.systems.halyksoap.model;

import javax.persistence.*;

@Entity
public class HalykSaveCardOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String amount;
    private String merchantCertId;
    private String merchantName;
    private String currencyCode;
    private String merchantId;
    private String abonentId;
    private String approve;
    @Column(columnDefinition = "text")
    private String merchantSign;
    private String bankName;
    @Column(columnDefinition = "text")
    private String bankSign;
    private String reference;
    private String responseCode;
    private String secure;
    private String cardBin;
    private String cardHash;
    private String expMonth;
    private String expYear;
    private String cardId;
    private String recepient;
    private String sessionId;
    private String cHash;
    private String approvalCode;
    private String timestamp;
    private String requestServiceId;
    private String responseServiceId;

    public String getAbonentId() {
        return abonentId;
    }

    public void setAbonentId(String abonentId) {
        this.abonentId = abonentId;
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

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
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

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getRecepient() {
        return recepient;
    }

    public void setRecepient(String recepient) {
        this.recepient = recepient;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getcHash() {
        return cHash;
    }

    public void setcHash(String cHash) {
        this.cHash = cHash;
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

    public String getRequestServiceId() {
        return requestServiceId;
    }

    public void setRequestServiceId(String requestServiceId) {
        this.requestServiceId = requestServiceId;
    }

    public String getResponseServiceId() {
        return responseServiceId;
    }

    public void setResponseServiceId(String responseServiceId) {
        this.responseServiceId = responseServiceId;
    }
}
