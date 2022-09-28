package kz.capitalpay.server.login.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TwoFactorAuth {
    @Id
    private Long userId;
    private String smscode;
    private String checkSms;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

    public String getCheckSms() {
        return checkSms;
    }

    public void setCheckSms(String checkSms) {
        this.checkSms = checkSms;
    }
}
