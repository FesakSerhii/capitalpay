package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

import java.time.LocalDateTime;
import java.util.List;

public class HalykDTO {
    private List<HalykFieldsDTO> fields;

    public List<HalykFieldsDTO> getFields() {
        return fields;
    }

    public void setFields(List<HalykFieldsDTO> fields) {
        this.fields = fields;
    }

}
