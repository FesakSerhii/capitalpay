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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public ResultDTO pageList() {
        try {
            Set<String> pageList = new HashSet<>();
            List<Translate> translateList = translateRepository.findAll();
            if (translateList != null && translateList.size() > 0) {
                for (Translate tr : translateList) {
                    pageList.add(tr.getPage());
                }
            }
            return new ResultDTO(true, pageList, 0);
        } catch (Exception e) {
            logger.error("Line number: {} \n{}", e.getStackTrace()[0].getLineNumber(), e.getMessage());
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
