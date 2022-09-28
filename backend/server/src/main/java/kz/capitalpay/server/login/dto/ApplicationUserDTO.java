package kz.capitalpay.server.login.dto;

import javax.validation.constraints.NotBlank;

public class ApplicationUserDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    private String sms;

    private boolean trustIp;

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

    public boolean isTrustIp() {
        return trustIp;
    }

    public void setTrustIp(boolean trustIp) {
        this.trustIp = trustIp;
    }
}
