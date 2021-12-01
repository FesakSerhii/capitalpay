package kz.capitalpay.server.dictionary.dto;

import javax.validation.constraints.NotBlank;

public class GetDictionaryDTO {
    @NotBlank
    String page;
    @NotBlank
    String lang;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
