package kz.capitalpay.server.pages.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.pages.model.StaticPage;
import kz.capitalpay.server.pages.repository.StaticPageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StaticPageService {

    Logger logger = LoggerFactory.getLogger(StaticPageService.class);

    @Autowired
    Gson gson;

    @Autowired
    StaticPageRepository staticPageRepository;


    public ResultDTO showAll() {
        try {
            List<StaticPage> staticPageList = staticPageRepository.findAll();
            if (staticPageList == null) {
                staticPageList = new ArrayList<>();
            }

            return new ResultDTO(true, staticPageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
