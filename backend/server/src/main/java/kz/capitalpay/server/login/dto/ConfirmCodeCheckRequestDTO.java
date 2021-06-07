package kz.capitalpay.server.login.dto;

import javax.validation.constraints.NotBlank;

public class ConfirmCodeCheckRequestDTO {
    @NotBlank
    String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
