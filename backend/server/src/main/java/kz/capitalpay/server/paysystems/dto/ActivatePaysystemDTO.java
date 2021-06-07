package kz.capitalpay.server.paysystems.dto;

import javax.validation.constraints.NotNull;

public class ActivatePaysystemDTO {
    @NotNull
    Long paysystemId;
    @NotNull
    Boolean enabled;

    public Long getPaysystemId() {
        return paysystemId;
    }

    public void setPaysystemId(Long paysystemId) {
        this.paysystemId = paysystemId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
