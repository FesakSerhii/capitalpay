package kz.capitalpay.server.paysystems.systems.halyksoap.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class PaymentXmlDto {
    private String merchantId;
    private String amount;
    private String approvalCode;
    private String reference;
    private String responseCode;
    private String secure;
    private String cardBin;
    private String cardHash;
    private String expMonth;
    private String expYear;
    private String cardId;
    private String abonentId;
    private String recepient;
    private String sessionId;
    private String approve;
    private String cHash;

    @XmlAttribute(name = "merchant_id")
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    @XmlAttribute(name = "amount")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @XmlAttribute(name = "approval_code")
    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    @XmlAttribute(name = "reference")
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @XmlAttribute(name = "response_code")
    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @XmlAttribute(name = "Secure")
    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    @XmlAttribute(name = "card_bin")
    public String getCardBin() {
        return cardBin;
    }

    public void setCardBin(String cardBin) {
        this.cardBin = cardBin;
    }

    @XmlAttribute(name = "CardHash")
    public String getCardHash() {
        return cardHash;
    }

    public void setCardHash(String cardHash) {
        this.cardHash = cardHash;
    }

    @XmlAttribute(name = "exp_month")
    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    @XmlAttribute(name = "exp_year")
    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    @XmlAttribute(name = "CardId")
    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    @XmlAttribute(name = "abonent_id")
    public String getAbonentId() {
        return abonentId;
    }

    public void setAbonentId(String abonentId) {
        this.abonentId = abonentId;
    }

    @XmlAttribute(name = "recepient")
    public String getRecepient() {
        return recepient;
    }

    public void setRecepient(String recepient) {
        this.recepient = recepient;
    }

    @XmlAttribute(name = "sessionid")
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @XmlAttribute(name = "approve")
    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }

    @XmlAttribute(name = "c_hash")
    public String getcHash() {
        return cHash;
    }

    public void setcHash(String cHash) {
        this.cHash = cHash;
    }

    @Override
    public String toString() {
        return "{" +
                "merchantId='" + merchantId + '\'' +
                ", amount='" + amount + '\'' +
                ", approvalCode='" + approvalCode + '\'' +
                ", reference='" + reference + '\'' +
                ", response_code='" + responseCode + '\'' +
                ", secure='" + secure + '\'' +
                ", cardBin='" + cardBin + '\'' +
                ", cardHash='" + cardHash + '\'' +
                ", expMonth='" + expMonth + '\'' +
                ", exp_year='" + expYear + '\'' +
                ", cardId='" + cardId + '\'' +
                ", abonentId='" + abonentId + '\'' +
                ", recepient='" + recepient + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", approve='" + approve + '\'' +
                ", cHash='" + cHash + '\'' +
                '}';
    }
}
