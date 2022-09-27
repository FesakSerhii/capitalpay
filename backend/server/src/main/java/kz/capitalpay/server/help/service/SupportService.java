package kz.capitalpay.server.help.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.files.model.FileStorage;
import kz.capitalpay.server.files.service.FileStorageService;
import kz.capitalpay.server.help.dto.*;
import kz.capitalpay.server.help.model.SupportAnswer;
import kz.capitalpay.server.help.model.SupportRequest;
import kz.capitalpay.server.help.repository.SupportAnswerRepository;
import kz.capitalpay.server.help.repository.SupportRequestRepository;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.service.SendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static kz.capitalpay.server.constants.ErrorDictionary.SUPPORT_REQUEST_NOT_FOUND;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.ANSWER_SUPPORT_REQUEST;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.CHANGE_STATUS_SUPPORT_REQUEST;

@Service
public class SupportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SupportService.class);
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

    @Autowired
    SystemEventsLogsService systemEventsLogsService;

    @Autowired
    SupportAnswerRepository supportAnswerRepository;

    @Value("${filestorage.remote.path}")
    String filePath;

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
            supportRequest.setTimestamp(System.currentTimeMillis());
            supportRequestRepository.save(supportRequest);
            return new ResultDTO(true, supportRequest.getId(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    public ResultDTO requestList() {
        try {
            List<SupportRequestDtoList> requestList = supportRequestRepository.findAll().stream()
                    .map(this::createSupportRequestDtoListObject)
                    .sorted(Comparator.comparing(SupportRequestDtoList::getTimestamp))
                    .collect(Collectors.toList());

            Collections.reverse(requestList);
            if (requestList.isEmpty()) {
                requestList = new ArrayList<>();
            }
            return new ResultDTO(true, requestList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO getRequestsByMerchantId(Long merchantId) {
        try {
            List<SupportRequestDtoList> requestList = supportRequestRepository.findAllByAuthorId(merchantId).stream()
                    .map(this::createSupportRequestDtoListObject)
                    .sorted(Comparator.comparing(SupportRequestDtoList::getTimestamp))
                    .collect(Collectors.toList());

            Collections.reverse(requestList);
            if (requestList.isEmpty()) {
                requestList = new ArrayList<>();
            }
            return new ResultDTO(true, requestList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public SupportRequestDtoList createSupportRequestDtoListObject(SupportRequest supportRequest) {
        ApplicationUser applicationUser = applicationUserService.getUserById(supportRequest.getAuthorId());
        SupportRequestDtoList supportRequestDtoList = new SupportRequestDtoList();
        supportRequestDtoList.setId(supportRequest.getId());
        supportRequestDtoList.setAuthorId(supportRequest.getAuthorId());
        supportRequestDtoList.setTimestamp(supportRequest.getTimestamp() == null ? System.currentTimeMillis() :
                supportRequest.getTimestamp());
        supportRequestDtoList.setEmail(applicationUser.getEmail());
        supportRequestDtoList.setUsername(applicationUser.getUsername());
        supportRequestDtoList.setTheme(supportRequest.getTheme());
        supportRequestDtoList.setSubject(supportRequest.getSubject());
        supportRequestDtoList.setText(supportRequest.getText());
        supportRequestDtoList.setStatus(supportRequest.getStatus());
        supportRequestDtoList.setFileIdList(supportRequest.getFileIdList());
        supportRequestDtoList.setImportant(supportRequest.isImportant());
        return supportRequestDtoList;
    }

    public ResultDTO oneRequest(OneRequestDTO request) {
        try {
            SupportRequest supportRequest = supportRequestRepository.findById(request.getId()).orElse(null);
            if (supportRequest == null) {
                return SUPPORT_REQUEST_NOT_FOUND;
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
            if (supportRequest.getFileIdList() != null || supportRequest.getFileIdList().length() != 0) {
                List<Double> doubleList = gson.fromJson(supportRequest.getFileIdList(), List.class);
                LOGGER.info(gson.toJson(doubleList));
                List<Long> filesIdList = new ArrayList<>();
                doubleList.forEach(aDouble -> {
                    if (Objects.nonNull(aDouble)) {
                        filesIdList.add(aDouble.longValue());
                    }
                });
                responceDTO.setFileList(fileStorageService.getFilListById(filesIdList));
            }

            List<SupportAnswer> supportAnswerList = supportAnswerRepository.findByRequestId(supportRequest.getId());
            if (supportAnswerList != null && supportAnswerList.size() > 0) {
                List<SupportAnswerDTO> supportAnswerDTOS = new ArrayList<>();
                for (SupportAnswer sa : supportAnswerList) {
                    SupportAnswerDTO supportAnswer = new SupportAnswerDTO();
                    supportAnswer.setId(sa.getId());
                    supportAnswer.setOperatorId(sa.getOperatorId());
                    supportAnswer.setText(sa.getText());
                    if (sa.getFileIdList() != null && sa.getFileIdList().length() > 0) {
                        List<Double> doubleList = gson.fromJson(sa.getFileIdList(), List.class);
                        List<Long> fileIdList = new ArrayList<>();
                        doubleList.forEach(aDouble -> {
                            if (Objects.nonNull(aDouble)) {
                                fileIdList.add(aDouble.longValue());
                            }
                        });
                        supportAnswer.setFileList(fileStorageService.getFilListById(fileIdList));
                    }
                    supportAnswerDTOS.add(supportAnswer);
                }
                responceDTO.setSupportAnswer(supportAnswerDTOS);
            }
            return new ResultDTO(true, responceDTO, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO changeStatus(Principal principal, ChangeStatusSupportRequestDTO request) {
        try {
            ApplicationUser operator = applicationUserService.getUserByLogin(principal.getName());
            SupportRequest supportRequest = supportRequestRepository.findById(request.getRequestId()).orElse(null);
            if (supportRequest == null) {
                return SUPPORT_REQUEST_NOT_FOUND;
            }
            ApplicationUser merchant = applicationUserService.getUserById(supportRequest.getAuthorId());
            if (request.getStatus() == 1) {
                supportRequest.setStatus(PROCESSED);
            } else if (request.getStatus() == 2) {
                supportRequest.setStatus(CLOSED);
            } else {
                supportRequest.setStatus(NEW_SUPPORT_REQUEST);
            }
            supportRequest.setImportant(request.isImportant());
            systemEventsLogsService.addNewOperatorAction(operator.getUsername(), CHANGE_STATUS_SUPPORT_REQUEST,
                    gson.toJson(request), merchant.getId().toString());
            supportRequestRepository.save(supportRequest);
            return new ResultDTO(true, supportRequest, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO sendSupportAnswer(Principal principal, SendSupportAnswerDTO request) {
        try {
            ApplicationUser operator = applicationUserService.getUserByLogin(principal.getName());
            SupportRequest supportRequest = supportRequestRepository.findById(request.getRequestId()).orElse(null);
            if (supportRequest == null) {
                return SUPPORT_REQUEST_NOT_FOUND;
            }

            ApplicationUser merchant = applicationUserService.getUserById(supportRequest.getAuthorId());
            SupportAnswer supportAnswer = new SupportAnswer();
            supportAnswer.setRequestId(supportRequest.getId());
            supportAnswer.setOperatorId(operator.getId());
            supportAnswer.setText(request.getText());
            supportAnswer.setFileIdList(gson.toJson(request.getFileList()));
            supportAnswerRepository.save(supportAnswer);

            StringBuilder sb = new StringBuilder(request.getText());
            if (request.getFileList() != null && request.getFileList().size() != 0) {
                sb.append("<p>Прикрепленные файлы:</p>");
                List<FileStorage> fileList = fileStorageService.getFilListById(request.getFileList());
                for (FileStorage file : fileList) {
                    sb.append("<p><a href=" + filePath + "/" + file.getPath() + ">" + file.getFilename() + "</a></p>");
                }
            }

            sendEmailService.sendMail(merchant.getEmail(), "CapitalPay: " + supportRequest.getSubject(),
                    sb.toString());
            systemEventsLogsService.addNewOperatorAction(operator.getUsername(), ANSWER_SUPPORT_REQUEST,
                    gson.toJson(request), merchant.getId().toString());
            supportRequest.setStatus(CLOSED);
            supportRequestRepository.save(supportRequest);
            return new ResultDTO(true, supportAnswer, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public List<SupportRequest> findAllByEmailMessageSentFalse() {
        return supportRequestRepository.findAllByEmailMessageSentFalse();
    }

    public List<SupportRequest> saveAllSupportRequests(List<SupportRequest> request) {
        return supportRequestRepository.saveAll(request);
    }
}
