package kz.capitalpay.server.login.dto;

import javax.validation.constraints.NotNull;

public class OneUserDTO {
    @NotNull
    Long id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
