package kz.capitalpay.server.payments.dto;

public class PaymentSortDto {
    private String field;
    private boolean asc;

    public PaymentSortDto(String field, boolean asc) {
        this.field = field;
        this.asc = asc;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }
}
