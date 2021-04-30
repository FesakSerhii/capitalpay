package kz.capitalpay.server.paysystems.systems.halykbank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class HalykPayment {
    @Id @GeneratedValue
    Long id;
    String halykId;
    Long timestamp;
    LocalDateTime localDateTime;
    String billId;
    String paymentId;
    Long cashboxId;
    @Column(precision = 30, scale = 2)
    BigDecimal totalAmount;
    String currency;
    String status;
    @Column(length = 16383)
    String form;
    @Column(length = 16383)
    String xmlResponse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHalykId() {
        return halykId;
    }

    public void setHalykId(String halykId) {
        this.halykId = halykId;
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

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Long getCashboxId() {
        return cashboxId;
    }

    public void setCashboxId(Long cashboxId) {
        this.cashboxId = cashboxId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getXmlResponse() {
        return xmlResponse;
    }

    public void setXmlResponse(String xmlResponse) {
        this.xmlResponse = xmlResponse;
    }
}
