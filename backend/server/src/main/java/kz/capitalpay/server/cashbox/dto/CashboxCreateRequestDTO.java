package kz.capitalpay.server.cashbox.dto;

import com.sun.istack.NotNull;

import javax.validation.constraints.NotBlank;

public class CashboxCreateRequestDTO {
    @NotBlank
    String name;
    @NotBlank
    String currency;

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
