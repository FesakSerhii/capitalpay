package kz.capitalpay.server.currency.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AddCurrencyDTO {
    @NotBlank
    String alpha;
    @NotBlank
    String number;
    @NotBlank
    String unicode;
    @NotBlank
    String name;
    @NotNull
    Boolean enabled;

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
