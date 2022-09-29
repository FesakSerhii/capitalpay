package kz.capitalpay.server.simple.dto;

import java.math.BigDecimal;

public class SimpleRequestDTO {
    private Long cashboxid;
    private String billid;
    private BigDecimal totalamount;
    private String currency;
    private String description;
    private String param;
    private String ipAddress;
    private String userAgent;

    public Long getCashboxid() {
        return cashboxid;
    }

    public void setCashboxid(Long cashboxid) {
        this.cashboxid = cashboxid;
    }

    public String getBillid() {
        return billid;
    }

    public void setBillid(String billid) {
        this.billid = billid;
    }

    public BigDecimal getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(BigDecimal totalamount) {
        this.totalamount = totalamount;
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
}
