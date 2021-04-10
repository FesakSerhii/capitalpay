package kz.capitalpay.server.help.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.help.dto.SupportRequestDTO;
import kz.capitalpay.server.help.model.SupportRequest;
import kz.capitalpay.server.help.repository.SupportRequestRepository;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.service.SendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class SupportService {

    Logger logger = LoggerFactory.getLogger(SupportService.class);

    public static final String NEW_SUPPORT_REQUEST = "new";
    public static final String PROCESSED = "processed";
    public static final String CLOSED = "closed";


    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    SupportRequestRepository supportRequestRepository;

    @Autowired
    SendEmailService sendEmailService;

    public ResultDTO supportRequest(Principal principal, SupportRequestDTO request) {
        try {
            ApplicationUser applicationUser = applicationUserService.getUserByLogin(principal.getName());

            SupportRequest supportRequest = new SupportRequest();
            supportRequest.setAuthorId(applicationUser.getId());
            supportRequest.setSubject(request.getSubject());
            supportRequest.setTheme(request.getTheme());
            supportRequest.setText(request.getText());
            supportRequest.setFileIdList(gson.toJson(request.getFileList()));
            supportRequest.setStatus(NEW_SUPPORT_REQUEST);

            supportRequestRepository.save(supportRequest);

            String text = String.format("Ваше обращение в службу поддержки принято.<br/>" +
                    "Ему присвоен номер: %s <br/>", supportRequest.getId());

            sendEmailService.sendMail(applicationUser.getEmail(), "CapitalPay", text);

            return new ResultDTO(true, supportRequest.getId(), 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }
}
