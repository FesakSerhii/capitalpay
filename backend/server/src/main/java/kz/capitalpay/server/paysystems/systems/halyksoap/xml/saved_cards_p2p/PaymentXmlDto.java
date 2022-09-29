package kz.capitalpay.server.paysystems.systems.halyksoap.xml.saved_cards_p2p;

import javax.xml.bind.annotation.XmlAttribute;

public class PaymentXmlDto {
    private String actionTime;
    private String orderId;
    private String merchantId;
    private String cardIdFrom;
    private String cardIdTo;
    private String cardHashFrom;
    private String cardHashTo;
    private String amount;
    private String amountWithFee;
    private String fee;
    private String currency;
    private String woFee;
    private String expDayFromCard;
    private String expDayToCard;
    private String terminal;
    private String result;
    private String message;
    private String reference;
    private String intReference;
    private String approvalCode;
    private String session;

    @XmlAttribute(name = "ActionTime")
    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    @XmlAttribute(name = "OrderId")
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @XmlAttribute(name = "MerchantID")
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    @XmlAttribute(name = "card_id_from")
    public String getCardIdFrom() {
        return cardIdFrom;
    }

    public void setCardIdFrom(String cardIdFrom) {
        this.cardIdFrom = cardIdFrom;
    }

    @XmlAttribute(name = "card_id_to")
    public String getCardIdTo() {
        return cardIdTo;
    }

    public void setCardIdTo(String cardIdTo) {
        this.cardIdTo = cardIdTo;
    }

    @XmlAttribute(name = "cardHash_from")
    public String getCardHashFrom() {
        return cardHashFrom;
    }

    public void setCardHashFrom(String cardHashFrom) {
        this.cardHashFrom = cardHashFrom;
    }

    @XmlAttribute(name = "cardHash_to")
    public String getCardHashTo() {
        return cardHashTo;
    }

    public void setCardHashTo(String cardHashTo) {
        this.cardHashTo = cardHashTo;
    }

    @XmlAttribute(name = "amount")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @XmlAttribute(name = "amount_with_fee")
    public String getAmountWithFee() {
        return amountWithFee;
    }

    public void setAmountWithFee(String amountWithFee) {
        this.amountWithFee = amountWithFee;
    }

    @XmlAttribute(name = "fee")
    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    @XmlAttribute(name = "currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @XmlAttribute(name = "wofee")
    public String getWoFee() {
        return woFee;
    }

    public void setWoFee(String woFee) {
        this.woFee = woFee;
    }

    @XmlAttribute(name = "exp_day_fromcard")
    public String getExpDayFromCard() {
        return expDayFromCard;
    }

    public void setExpDayFromCard(String expDayFromCard) {
        this.expDayFromCard = expDayFromCard;
    }

    @XmlAttribute(name = "exp_day_tocard")
    public String getExpDayToCard() {
        return expDayToCard;
    }

    public void setExpDayToCard(String expDayToCard) {
        this.expDayToCard = expDayToCard;
    }

    @XmlAttribute(name = "terminal")
    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    @XmlAttribute(name = "Result")
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @XmlAttribute(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlAttribute(name = "reference")
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @XmlAttribute(name = "int_reference")
    public String getIntReference() {
        return intReference;
    }

    public void setIntReference(String intReference) {
        this.intReference = intReference;
    }

    @XmlAttribute(name = "approval_code")
    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    @XmlAttribute(name = "session")
    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "{" + "ActionTime='" + actionTime + '\'' + ", OrderId='" + orderId + '\'' + ", MerchantID='" + merchantId + '\'' + ", card_id_from='" + cardIdFrom + '\'' + ", card_id_to='" + cardIdTo + '\'' + ", cardHash_from='" + cardHashFrom + '\'' + ", cardHash_to='" + cardHashTo + '\'' + ", amount='" + amount + '\'' + ", amount_with_fee='" + amountWithFee + '\'' + ", fee='" + fee + '\'' + ", currency='" + currency + '\'' + ", wofee='" + woFee + '\'' + ", exp_day_fromcard='" + expDayFromCard + '\'' + ", exp_day_tocard='" + expDayToCard + '\'' + ", terminal='" + terminal + '\'' + ", Result='" + result + '\'' + ", message='" + message + '\'' + ", reference='" + reference + '\'' + ", int_reference='" + intReference + '\'' + ", approval_code='" + approvalCode + '\'' + ", session='" + session + '\'' + '}';
    }
}
