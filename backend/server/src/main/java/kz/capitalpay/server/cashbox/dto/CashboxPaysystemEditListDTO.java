package kz.capitalpay.server.cashbox.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CashboxPaysystemEditListDTO {

    @NotNull
    Long cashboxId;

    @NotNull
    List<Long> paysystemList;

    public Long getCashboxId() {
        return cashboxId;
    }

    public void setCashboxId(Long cashboxId) {
        this.cashboxId = cashboxId;
    }

    public List<Long> getPaysystemList() {
        return paysystemList;
    }

    public void setPaysystemList(List<Long> paysystemList) {
        this.paysystemList = paysystemList;
    }
}
