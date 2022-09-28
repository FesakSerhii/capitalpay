package kz.capitalpay.server.login.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class SignUpPhoneRequestDTO {
    @NotBlank
    private String code;
    @Pattern(regexp = "[+]\\d{11}", message = "Phone number standard E.164: +77012345678")
    private String phone;
    @NotBlank
    private String password;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
