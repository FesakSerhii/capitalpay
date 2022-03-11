package kz.capitalpay.server.payments.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    private String guid;
    private Long timestamp;
    private LocalDateTime localDateTime;
    private Long merchantId;
    private String merchantName;
    private Long cashboxId;
    private String cashboxName;
    private String billId;
    private String paySysPayId;
    @Column(precision = 30, scale = 2)
    private BigDecimal totalAmount;
    private String currency;
    private String description;
    private String phone;
    private String email;
    private String param;
    private String ipAddress;
    private String userAgent;
    private String status;
    @Column(columnDefinition = "boolean default false")
    boolean toClient;
    @Column(columnDefinition = "boolean default false")
    boolean p2p;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Long getCashboxId() {
        return cashboxId;
    }

    public void setCashboxId(Long cashboxId) {
        this.cashboxId = cashboxId;
    }

    public String getCashboxName() {
        return cashboxName;
    }

    public void setCashboxName(String cashboxName) {
        this.cashboxName = cashboxName;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getPaySysPayId() {
        return paySysPayId;
    }

    public void setPaySysPayId(String paySysPayId) {
        this.paySysPayId = paySysPayId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isToClient() {
        return toClient;
    }

    public void setToClient(boolean toClient) {
        this.toClient = toClient;
    }

    public boolean isP2p() {
        return p2p;
    }

    public void setP2p(boolean p2p) {
        this.p2p = p2p;
    }
}
