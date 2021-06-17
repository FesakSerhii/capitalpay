package kz.capitalpay.server.cashbox.dto;

import javax.validation.constraints.NotBlank;

public class CashboxCreateRequestDTO {
    long merchantId;
    @NotBlank
    String name;
    @NotBlank
    String currency;

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
