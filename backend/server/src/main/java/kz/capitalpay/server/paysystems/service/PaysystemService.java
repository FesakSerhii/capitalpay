package kz.capitalpay.server.paysystems.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.paysystems.dto.ActivatePaysystemDTO;
import kz.capitalpay.server.paysystems.model.Paysystem;
import kz.capitalpay.server.paysystems.repository.PaysystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static kz.capitalpay.server.constants.ErrorDictionary.error114;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.ACTIVATE_PAYSYSTEM;

@Service
public class PaysystemService {

    Logger logger = LoggerFactory.getLogger(PaysystemService.class);

    @Autowired
    Gson gson;

    @Autowired
    PaysystemRepository paysystemRepository;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;

    public ResultDTO systemList() {
        try {
            List<Paysystem> systemList = paysystemList();
            return new ResultDTO(true, systemList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    List<Paysystem> paysystemList(){
        List<Paysystem> paysystemList = paysystemRepository.findAll();
        if (paysystemList == null || paysystemList.size() == 0) {
            paysystemList = new ArrayList<>();
            Paysystem paysystem = new Paysystem();
            paysystem.setName("Test PaySystem");
            paysystem.setEnabled(true);
            paysystemRepository.save(paysystem);
            paysystemList.add(paysystem);
        }
        return paysystemList;
    }


    public ResultDTO enablePaysystem(Principal principal, ActivatePaysystemDTO request) {
        try {
            ApplicationUser operator = applicationUserService.getUserByLogin(principal.getName());
            Paysystem paysystem = paysystemRepository.findById(request.getPaysystemId()).orElse(null);
            if (paysystem == null) {
                return error114;
            }
            paysystem.setEnabled(request.getEnabled());

            systemEventsLogsService.addNewOperatorAction(operator.getUsername(),ACTIVATE_PAYSYSTEM,
                    gson.toJson(request),"all");

            paysystemRepository.save(paysystem);

            return new ResultDTO(true, paysystem, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
