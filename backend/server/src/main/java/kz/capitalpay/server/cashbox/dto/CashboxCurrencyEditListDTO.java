package kz.capitalpay.server.cashbox.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CashboxCurrencyEditListDTO {

    @NotNull
    Long cashboxId;

    @NotNull
    List<String> currencyList;

    public Long getCashboxId() {
        return cashboxId;
    }

    public void setCashboxId(Long cashboxId) {
        this.cashboxId = cashboxId;
    }

    public List<String> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<String> currencyList) {
        this.currencyList = currencyList;
    }
}
