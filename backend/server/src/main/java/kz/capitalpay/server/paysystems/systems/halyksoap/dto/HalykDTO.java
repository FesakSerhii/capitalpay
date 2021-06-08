package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

import java.util.List;

public class HalykDTO {
    private long halykId;
    private List<HalykFieldsDTO> fields;

    public long getHalykId() {
        return halykId;
    }

    public void setHalykId(long halykId) {
        this.halykId = halykId;
    }

    public List<HalykFieldsDTO> getFields() {
        return fields;
    }

    public void setFields(List<HalykFieldsDTO> fields) {
        this.fields = fields;
    }
}
