package kz.capitalpay.server.pages.dto;

import javax.validation.constraints.NotBlank;

public class DeleteDTO {
    @NotBlank
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
