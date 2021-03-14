package kz.capitalpay.server.dictionary.dto;

import java.util.List;

public class SaveTranslateRequestDTO {
    List<SaveOneRequestDTO> translates;

    public List<SaveOneRequestDTO> getTranslates() {
        return translates;
    }

    public void setTranslates(List<SaveOneRequestDTO> translates) {
        this.translates = translates;
    }
}
