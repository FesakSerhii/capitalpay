package kz.capitalpay.server.dictionary.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dictionary.dto.SaveOneRequestDTO;
import kz.capitalpay.server.dictionary.dto.SaveTranslateRequestDTO;
import kz.capitalpay.server.dictionary.model.Translate;
import kz.capitalpay.server.dictionary.repository.TranslateRepository;
import kz.capitalpay.server.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranslateService {

    Logger logger = LoggerFactory.getLogger(TranslateService.class);

    @Autowired
    Gson gson;

    @Autowired
    TranslateRepository translateRepository;

    public ResultDTO save(SaveTranslateRequestDTO request) {
        try {
            for (SaveOneRequestDTO tr : request.getTranslates()) {
                Translate translate = translateRepository.findTopByPageAndTranskeyAndLang(
                        tr.getPage(), tr.getTranskey(), tr.getLang());
                if (translate == null) {
                    translate = new Translate();
                    translate.setPage(tr.getPage());
                    translate.setTranskey(tr.getTranskey());
                    translate.setLang(tr.getLang());
                }
                translate.setText(tr.getText());
                translateRepository.save(translate);
            }
            return new ResultDTO(true, String.format("Saved %d records", request.getTranslates().size()), 0);
        } catch (Exception e) {
            logger.error("Line number: {} \n{}", e.getStackTrace()[0].getLineNumber(), e.getMessage());
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
