package kz.capitalpay.server.payments.dto;

import javax.validation.constraints.NotNull;

public class OnePaymentDetailsRequestDTO {
    @NotNull
    private String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
