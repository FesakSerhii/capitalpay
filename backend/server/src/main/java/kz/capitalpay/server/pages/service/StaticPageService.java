package kz.capitalpay.server.pages.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.pages.dto.DeleteDTO;
import kz.capitalpay.server.pages.dto.SavePageDTO;
import kz.capitalpay.server.pages.dto.ShowListDTO;
import kz.capitalpay.server.pages.dto.ShowOnePageDTO;
import kz.capitalpay.server.pages.model.StaticPage;
import kz.capitalpay.server.pages.repository.StaticPageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kz.capitalpay.server.constants.ErrorDictionary.PAGE_NOT_FOUND;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.*;

@Service
public class StaticPageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticPageService.class);

    private final Gson gson;
    private final StaticPageRepository staticPageRepository;
    private final ApplicationUserService applicationUserService;
    private final SystemEventsLogsService systemEventsLogsService;

    public StaticPageService(Gson gson, StaticPageRepository staticPageRepository, ApplicationUserService applicationUserService, SystemEventsLogsService systemEventsLogsService) {
        this.gson = gson;
        this.staticPageRepository = staticPageRepository;
        this.applicationUserService = applicationUserService;
        this.systemEventsLogsService = systemEventsLogsService;
    }


    public ResultDTO showAll(ShowListDTO request) {
        try {
            List<StaticPage> staticPageList = staticPageRepository.findByLanguage(request.getLanguage());
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
                systemEventsLogsService.addNewOperatorAction(applicationUser.getUsername(), CREATE_STATIC_PAGE, gson.toJson(request), "all");
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
            systemEventsLogsService.addNewOperatorAction(applicationUser.getUsername(), EDIT_STATIC_PAGE, gson.toJson(request), "all");
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
                return PAGE_NOT_FOUND;
            }
            return new ResultDTO(true, staticPage, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO delete(Principal principal, DeleteDTO request) {
        try {
            ApplicationUser applicationUser = applicationUserService.getUserByLogin(principal.getName());
            List<StaticPage> staticPageList = staticPageRepository.findByTag(request.getTag());
            if (staticPageList == null) {
                return PAGE_NOT_FOUND;
            }
            systemEventsLogsService.addNewOperatorAction(applicationUser.getUsername(), DELETE_STATIC_PAGE, gson.toJson(request), "all");
            staticPageRepository.deleteAll(staticPageList);
            return new ResultDTO(true, staticPageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
