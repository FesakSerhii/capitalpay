package kz.capitalpay.server.cashbox.dto;

import javax.validation.constraints.NotNull;

public class CashboxCurrencyRequestDTO {

    @NotNull
    Long cashboxId;

    public Long getCashboxId() {
        return cashboxId;
    }

    public void setCashboxId(Long cashboxId) {
        this.cashboxId = cashboxId;
    }
}
