package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

import javax.validation.constraints.NotBlank;

public class HalykFieldsDTO {
    @NotBlank
    private String fieldName;
    @NotBlank
    private String fieldValue;

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
