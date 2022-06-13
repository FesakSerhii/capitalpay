package kz.capitalpay.server.cashbox.dto;

public class CashBoxFeeListDto {
    private Long cashBoxId;
    private String cashBoxName;
    private String totalFee;
    private String merchantFee;
    private String clientFee;

    public CashBoxFeeListDto(Long cashBoxId, String cashBoxName, String totalFee, String clientFee, String merchantFee) {
        this.cashBoxId = cashBoxId;
        this.cashBoxName = cashBoxName;
        this.totalFee = totalFee;
        this.clientFee = clientFee;
        this.merchantFee = merchantFee;
    }

    public Long getCashBoxId() {
        return cashBoxId;
    }

    public void setCashBoxId(Long cashBoxId) {
        this.cashBoxId = cashBoxId;
    }

    public String getCashBoxName() {
        return cashBoxName;
    }

    public void setCashBoxName(String cashBoxName) {
        this.cashBoxName = cashBoxName;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getMerchantFee() {
        return merchantFee;
    }

    public void setMerchantFee(String merchantFee) {
        this.merchantFee = merchantFee;
    }

    public String getClientFee() {
        return clientFee;
    }

    public void setClientFee(String clientFee) {
        this.clientFee = clientFee;
    }
}
