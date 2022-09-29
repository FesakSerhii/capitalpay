package kz.capitalpay.server.paysystems.dto;

import javax.validation.constraints.NotNull;

public class ActivatePaysystemDTO {
    @NotNull
    private Long paysystemId;
    @NotNull
    private Boolean enabled;

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
