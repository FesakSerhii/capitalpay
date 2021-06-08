package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

import java.time.LocalDateTime;
import java.util.List;

public class HalykDTO {
    private List<HalykFieldsDTO> fields;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    public List<HalykFieldsDTO> getFields() {
        return fields;
    }

    public void setFields(List<HalykFieldsDTO> fields) {
        this.fields = fields;
    }

    public LocalDateTime getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDateTime getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }
}
