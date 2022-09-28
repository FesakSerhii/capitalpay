package kz.capitalpay.server.pages.dto;

import javax.validation.constraints.NotBlank;

public class ShowListDTO {
    @NotBlank
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
