package kz.capitalpay.server.help.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.files.model.FileStorage;
import kz.capitalpay.server.files.service.FileStorageService;
import kz.capitalpay.server.help.dto.OneRequestDTO;
import kz.capitalpay.server.help.dto.OneSupportRequestResponceDTO;
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
import java.util.ArrayList;
import java.util.List;

import static kz.capitalpay.server.constants.ErrorDictionary.error109;

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

    @Autowired
    FileStorageService fileStorageService;

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


    public ResultDTO requestList() {
        try {
            List<SupportRequest> requestList = supportRequestRepository.findAll();
            if (requestList == null) requestList = new ArrayList<>();

            return new ResultDTO(true, requestList, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO oneRequest(OneRequestDTO request) {
        try {
            SupportRequest supportRequest = supportRequestRepository.findById(request.getId()).orElse(null);
            if (supportRequest == null) {
                return error109;
            }

            ApplicationUser applicationUser = applicationUserService.getUserById(supportRequest.getAuthorId());
            applicationUser.setPassword(null);

            OneSupportRequestResponceDTO responceDTO = new OneSupportRequestResponceDTO();
            responceDTO.setAuthor(applicationUser);
            responceDTO.setId(supportRequest.getId());
            responceDTO.setStatus(supportRequest.getStatus());
            responceDTO.setSubject(supportRequest.getSubject());
            responceDTO.setText(supportRequest.getText());
            responceDTO.setTheme(supportRequest.getTheme());
            responceDTO.setFileList(new ArrayList<>());
            if(supportRequest.getFileIdList()!=null || supportRequest.getFileIdList().length()!=0) {


                List<Long> filesIdList = gson.fromJson(supportRequest.getFileIdList(), List.class);
                logger.info(gson.toJson(filesIdList));

                responceDTO.setFileList(fileStorageService.getFilListById(filesIdList));

            }
            return new ResultDTO(true, responceDTO, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
