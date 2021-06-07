package kz.capitalpay.server.cashbox.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CashboxNameRequestDTO {

    @NotNull
    Long cashboxId;
    @NotBlank
    String name;

    public Long getCashboxId() {
        return cashboxId;
    }

    public void setCashboxId(Long cashboxId) {

        this.cashboxId = cashboxId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
