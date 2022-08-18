package kz.capitalpay.server.p2p.dto;

import javax.validation.constraints.NotNull;

public class IdDto {
    @NotNull
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
