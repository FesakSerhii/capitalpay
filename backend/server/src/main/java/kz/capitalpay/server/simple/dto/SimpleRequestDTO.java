package kz.capitalpay.server.simple.dto;

import org.springframework.web.bind.annotation.RequestParam;

public class SimpleRequestDTO {
    Long cashboxid;
    String billid;
    Long totalamount;
    String currency;
    String param;
    String ipAddress;
    String userAgent;

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

    public Long getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(Long totalamount) {
        this.totalamount = totalamount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
