package kz.capitalpay.server.pages.dto;

import javax.validation.constraints.NotBlank;

public class SavePageDTO {
    @NotBlank
    String language;
    @NotBlank
    String tag;
    @NotBlank
    String name;
    @NotBlank
    String content;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
