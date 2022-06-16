package kz.capitalpay.server.cashbox.dto;

import javax.validation.constraints.NotNull;

public class CashboxRequestDTO {

    @NotNull
    private Long cashboxId;

    public Long getCashboxId() {
        return cashboxId;
    }

    public void setCashboxId(Long cashboxId) {
        this.cashboxId = cashboxId;
    }

    @Override
    public String toString() {
        return "CashboxRequestDTO{" +
                "cashboxId=" + cashboxId +
                '}';
    }
}
