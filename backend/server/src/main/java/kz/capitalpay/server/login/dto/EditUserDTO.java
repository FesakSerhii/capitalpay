package kz.capitalpay.server.login.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class EditUserDTO {
    @NotBlank
    Long id;

    String password;
    @Email
    @NotBlank
    String email;
    @Pattern(regexp = "[+]\\d{11}", message = "Phone number standard E.164: +77012345678")
    String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
