package kz.capitalpay.server.pages.dto;

import javax.validation.constraints.NotBlank;

public class ShowOnePageDTO {
    @NotBlank
    String language;
    @NotBlank
    String tag;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
