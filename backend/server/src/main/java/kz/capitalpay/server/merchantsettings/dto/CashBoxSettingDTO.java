package kz.capitalpay.server.merchantsettings.dto;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class CashBoxSettingDTO {
    @NotEmpty
    private long cashBoxId;
    private List<CashBoxSettingFieldDTO> fields;

    public long getCashBoxId() {
        return cashBoxId;
    }

    public void setCashBoxId(long cashBoxId) {
        this.cashBoxId = cashBoxId;
    }

    public List<CashBoxSettingFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<CashBoxSettingFieldDTO> fields) {
        this.fields = fields;
    }
}
