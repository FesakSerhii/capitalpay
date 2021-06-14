package kz.capitalpay.server.merchantsettings.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class CashBoxSettingFieldDTO {
    @NotEmpty
    private long cashBoxId;
    @NotBlank
    private String fieldName;
    @NotBlank
    private String fieldValue;

    public long getCashBoxId() {
        return cashBoxId;
    }

    public void setCashBoxId(long cashBoxId) {
        this.cashBoxId = cashBoxId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}
