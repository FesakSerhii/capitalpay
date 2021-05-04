package kz.capitalpay.server.pages.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.pages.dto.SavePageDTO;
import kz.capitalpay.server.pages.dto.ShowOnePageDTO;
import kz.capitalpay.server.pages.model.StaticPage;
import kz.capitalpay.server.pages.repository.StaticPageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kz.capitalpay.server.constants.ErrorDictionary.error119;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.CREATE_STATIC_PAGE;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.EDIT_STATIC_PAGE;

@Service
public class StaticPageService {

    Logger logger = LoggerFactory.getLogger(StaticPageService.class);

    @Autowired
    Gson gson;

    @Autowired
    StaticPageRepository staticPageRepository;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;


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

    public ResultDTO showOne(Principal principal, ShowOnePageDTO request) {
        try {
            StaticPage staticPage = staticPageRepository.findTopByTagAndLanguage(request.getTag(), request.getLanguage());
            if (staticPage == null) {

                ApplicationUser applicationUser = applicationUserService.getUserByLogin(principal.getName());

                staticPage = new StaticPage();
                staticPage.setTag(request.getTag());
                staticPage.setLanguage(request.getLanguage());
                staticPage.setLocalDateTime(LocalDateTime.now());
                staticPage.setTimestamp(System.currentTimeMillis());
                staticPage.setName("");
                staticPage.setContent("");

                systemEventsLogsService.addNewOperatorAction(applicationUser.getUsername(), CREATE_STATIC_PAGE,
                        gson.toJson(request), "all");
                staticPageRepository.save(staticPage);
            }
            return new ResultDTO(true, staticPage, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    public ResultDTO save(Principal principal, SavePageDTO request) {
        try {
            StaticPage staticPage = staticPageRepository.findTopByTagAndLanguage(request.getTag(), request.getLanguage());
            ApplicationUser applicationUser = applicationUserService.getUserByLogin(principal.getName());
            if (staticPage == null) {
                staticPage = new StaticPage();
                staticPage.setTag(request.getTag());
                staticPage.setLanguage(request.getLanguage());
                staticPage.setLocalDateTime(LocalDateTime.now());
                staticPage.setTimestamp(System.currentTimeMillis());
            }
            staticPage.setContent(request.getContent());
            staticPage.setName(request.getName());

            systemEventsLogsService.addNewOperatorAction(applicationUser.getUsername(), EDIT_STATIC_PAGE,
                    gson.toJson(request), "all");

            staticPageRepository.save(staticPage);
            return new ResultDTO(true, staticPage, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO showPage(ShowOnePageDTO request) {
        try {
            StaticPage staticPage = staticPageRepository.findTopByTagAndLanguage(request.getTag(), request.getLanguage());
            if (staticPage == null) {
                return error119;
            }
            return new ResultDTO(true, staticPage, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }
}
