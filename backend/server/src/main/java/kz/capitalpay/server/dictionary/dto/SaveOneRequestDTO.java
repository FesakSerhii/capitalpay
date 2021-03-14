package kz.capitalpay.server.dictionary.dto;

import javax.validation.constraints.NotBlank;

public class SaveOneRequestDTO {
    @NotBlank
    String page;
    @NotBlank
    String lang;
    @NotBlank
    String transkey;
    @NotBlank
    String text;

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

    public String getTranskey() {
        return transkey;
    }

    public void setTranskey(String transkey) {
        this.transkey = transkey;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
