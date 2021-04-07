package kz.capitalpay.server.login.dto;

import javax.validation.constraints.NotBlank;

public class ApplicationUserDTO {
    @NotBlank
    String username;
    @NotBlank
    String password;

    String sms;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }
}
