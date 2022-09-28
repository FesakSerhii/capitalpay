package kz.capitalpay.server.login.dto;

import javax.validation.constraints.NotBlank;

public class NewPasswordRequestDTO {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
