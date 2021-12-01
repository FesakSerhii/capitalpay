package kz.capitalpay.server.login.dto;

public class TwoFactorAuthDTO {
    boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
