package kz.capitalpay.server.paysystems.systems.halyksoap.model;

import javax.persistence.*;

@Entity
public class HalykSavedCardsP2pOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String amount;
    private String merchantCertId;
    private String merchantName;
    private String currencyCode;
    private String merchantId;
    private String merchantMain;
    private String abonentIdFrom;
    private String abonentIdTo;
    private String cardIdFrom;
    private String cardIdTo;
    @Column(columnDefinition = "text")
    private String merchantSign;
    @Column(columnDefinition = "text")
    private String SignedOrderB64;
    private String actionTime;
    private String cardHashFrom;
    private String cardHashTo;
    private String amountWithFee;
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

    public String getMerchantMain() {
        return merchantMain;
    }

    public void setMerchantMain(String merchantMain) {
        this.merchantMain = merchantMain;
    }

    public String getAbonentIdFrom() {
        return abonentIdFrom;
    }

    public void setAbonentIdFrom(String abonentIdFrom) {
        this.abonentIdFrom = abonentIdFrom;
    }

    public String getAbonentIdTo() {
        return abonentIdTo;
    }

    public void setAbonentIdTo(String abonentIdTo) {
        this.abonentIdTo = abonentIdTo;
    }

    public String getCardIdFrom() {
        return cardIdFrom;
    }

    public void setCardIdFrom(String cardIdFrom) {
        this.cardIdFrom = cardIdFrom;
    }

    public String getCardIdTo() {
        return cardIdTo;
    }

    public void setCardIdTo(String cardIdTo) {
        this.cardIdTo = cardIdTo;
    }

    public String getMerchantSign() {
        return merchantSign;
    }

    public void setMerchantSign(String merchantSign) {
        this.merchantSign = merchantSign;
    }

    public String getSignedOrderB64() {
        return SignedOrderB64;
    }

    public void setSignedOrderB64(String signedOrderB64) {
        SignedOrderB64 = signedOrderB64;
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }

    public String getCardHashFrom() {
        return cardHashFrom;
    }

    public void setCardHashFrom(String cardHashFrom) {
        this.cardHashFrom = cardHashFrom;
    }

    public String getCardHashTo() {
        return cardHashTo;
    }

    public void setCardHashTo(String cardHashTo) {
        this.cardHashTo = cardHashTo;
    }

    public String getAmountWithFee() {
        return amountWithFee;
    }

    public void setAmountWithFee(String amountWithFee) {
        this.amountWithFee = amountWithFee;
    }

    public String getWoFee() {
        return woFee;
    }

    public void setWoFee(String woFee) {
        this.woFee = woFee;
    }

    public String getExpDayFromCard() {
        return expDayFromCard;
    }

    public void setExpDayFromCard(String expDayFromCard) {
        this.expDayFromCard = expDayFromCard;
    }

    public String getExpDayToCard() {
        return expDayToCard;
    }

    public void setExpDayToCard(String expDayToCard) {
        this.expDayToCard = expDayToCard;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getIntReference() {
        return intReference;
    }

    public void setIntReference(String intReference) {
        this.intReference = intReference;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
