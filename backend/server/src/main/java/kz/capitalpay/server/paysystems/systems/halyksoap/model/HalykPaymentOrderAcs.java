package kz.capitalpay.server.paysystems.systems.halyksoap.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class HalykPaymentOrderAcs {
    @Id
    @GeneratedValue
    Long id;
    Long timestamp;
    LocalDateTime localDateTime;

    //===Request=======
    String md;
    @Column(columnDefinition = "text")
    String pares;
    String sessionid;

    //===Response=======
    String acsUrl;
    String approvalcode;
    String intreference;
    boolean is3ds;
    String message;
    String orderid;
    String pareq;
    String reference;
    String returnCode;
    String termUrl;

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

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
    }

    public String getPares() {
        return pares;
    }

    public void setPares(String pares) {
        this.pares = pares;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
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
}
