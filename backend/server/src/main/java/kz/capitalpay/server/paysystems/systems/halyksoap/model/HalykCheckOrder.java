package kz.capitalpay.server.paysystems.systems.halyksoap.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class HalykCheckOrder {
    @Id
    @GeneratedValue
    private Long id;
    private Long timestamp;
    private LocalDateTime localDateTime;
    //===Request=======
    private String merchantid;
    private String orderid;

    //===Response======
    private String acceptReversalDate;
    private String approvalcode;
    private String cardhash;
    private String intreference;
    private String message;
    private String orderal;
    private String paidAmount;
    private String paidCurrency;
    private String payerip;
    private String payermail;
    private String payername;
    private String payerphone;
    private String reference;
    private String refundTotalAmount;
    private String resultcode;
    private String secure;
    private String sessionDate;
    private String sessionId;
    private String status;
    private String transactionDate;

    //===Signature=======
    private boolean signatureValid;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getMerchantid() {
        return merchantid;
    }

    public void setMerchantid(String merchantid) {
        this.merchantid = merchantid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getAcceptReversalDate() {
        return acceptReversalDate;
    }

    public void setAcceptReversalDate(String acceptReversalDate) {
        this.acceptReversalDate = acceptReversalDate;
    }

    public String getApprovalcode() {
        return approvalcode;
    }

    public void setApprovalcode(String approvalcode) {
        this.approvalcode = approvalcode;
    }

    public String getCardhash() {
        return cardhash;
    }

    public void setCardhash(String cardhash) {
        this.cardhash = cardhash;
    }

    public String getIntreference() {
        return intreference;
    }

    public void setIntreference(String intreference) {
        this.intreference = intreference;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderal() {
        return orderal;
    }

    public void setOrderal(String orderal) {
        this.orderal = orderal;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPaidCurrency() {
        return paidCurrency;
    }

    public void setPaidCurrency(String paidCurrency) {
        this.paidCurrency = paidCurrency;
    }

    public String getPayerip() {
        return payerip;
    }

    public void setPayerip(String payerip) {
        this.payerip = payerip;
    }

    public String getPayermail() {
        return payermail;
    }

    public void setPayermail(String payermail) {
        this.payermail = payermail;
    }

    public String getPayername() {
        return payername;
    }

    public void setPayername(String payername) {
        this.payername = payername;
    }

    public String getPayerphone() {
        return payerphone;
    }

    public void setPayerphone(String payerphone) {
        this.payerphone = payerphone;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getRefundTotalAmount() {
        return refundTotalAmount;
    }

    public void setRefundTotalAmount(String refundTotalAmount) {
        this.refundTotalAmount = refundTotalAmount;
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public boolean isSignatureValid() {
        return signatureValid;
    }

    public void setSignatureValid(boolean signatureValid) {
        this.signatureValid = signatureValid;
    }
}
