package kz.capitalpay.server.paysystems.systems.halyksoap.xml.halyk_control_order_xml;

import javax.xml.bind.annotation.XmlAttribute;

public class PaymentXmlDto {
    private String reference;
    private String approvalCode;
    private String orderId;
    private String amount;
    private String currencyCode;

    @XmlAttribute(name = "reference")
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @XmlAttribute(name = "approval_code")
    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    @XmlAttribute(name = "orderid")
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @XmlAttribute(name = "amount")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @XmlAttribute(name = "currency_code")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public String toString() {
        return "PaymentXmlDto{" +
                "reference='" + reference + '\'' +
                ", approvalCode='" + approvalCode + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount='" + amount + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
