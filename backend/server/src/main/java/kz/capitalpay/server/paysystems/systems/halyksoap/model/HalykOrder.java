package kz.capitalpay.server.paysystems.systems.halyksoap.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class HalykOrder {
    @Id
    @GeneratedValue
    private Long id;
    private Long timestamp;
    private LocalDateTime localDateTime;

    //===Request=======
    private String amount;
    private String cardholderName;
    private String currency;
    @Column(name = "description")
    private String desc;
    private String merchantid;
    private String orderid;
    private int trtype;

    //===Response=======
    private String acsUrl;
    private String approvalcode;
    private String intreference;
    private boolean is3ds;
    private String md;
    private String message;
    private String pareq;
    private String reference;
    private String returnCode;
    private String sessionid;
    private String termUrl;
    private String requestType;

    //===Signature=======
    boolean signatureValid;

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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public int getTrtype() {
        return trtype;
    }

    public void setTrtype(int trtype) {
        this.trtype = trtype;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    public void setAcsUrl(String acsUrl) {
        this.acsUrl = acsUrl;
    }

    public String getApprovalcode() {
        return approvalcode;
    }

    public void setApprovalcode(String approvalcode) {
        this.approvalcode = approvalcode;
    }

    public String getIntreference() {
        return intreference;
    }

    public void setIntreference(String intreference) {
        this.intreference = intreference;
    }

    public boolean isIs3ds() {
        return is3ds;
    }

    public void setIs3ds(boolean is3ds) {
        this.is3ds = is3ds;
    }

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPareq() {
        return pareq;
    }

    public void setPareq(String pareq) {
        this.pareq = pareq;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getTermUrl() {
        return termUrl;
    }

    public void setTermUrl(String termUrl) {
        this.termUrl = termUrl;
    }

    public boolean isSignatureValid() {
        return signatureValid;
    }

    public void setSignatureValid(boolean signatureValid) {
        this.signatureValid = signatureValid;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requeestType) {
        this.requestType = requeestType;
    }
}
