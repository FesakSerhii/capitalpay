package kz.capitalpay.server.merchantsettings.dto;

import javax.validation.constraints.NotBlank;

public class MerchantKycFieldDTO {
    @NotBlank
    private String fieldName;
    @NotBlank
    private String fieldValue;

    public MerchantKycFieldDTO(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
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
