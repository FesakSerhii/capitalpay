package kz.capitalpay.server.login.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class SignUpEmailRequestDTO {

    @Email
    @NotBlank
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
