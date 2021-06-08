package kz.capitalpay.server.merchantsettings.dto;

import javax.validation.constraints.NotEmpty;

public class CashBoxSettingDTO {
    @NotEmpty
    private long cashBoxId;
    @NotEmpty
    private String fieldValue;

    public long getCashBoxId() {
        return cashBoxId;
    }

    public void setCashBoxId(long cashBoxId) {
        this.cashBoxId = cashBoxId;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}
