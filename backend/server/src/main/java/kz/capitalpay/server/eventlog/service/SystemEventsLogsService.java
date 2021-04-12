package kz.capitalpay.server.eventlog.service;

import kz.capitalpay.server.eventlog.model.OperatorsAction;
import kz.capitalpay.server.eventlog.repository.OperatorActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemEventsLogsService {

    @Autowired
    OperatorActionRepository operatorActionRepository;

    public static final String LOGIN = "login";
    public static final String CHANGE_ROLE = "change role";
    public static final String CREATE_USER = "create user";
    public static final String DELETE_USER = "delete user";
    public static final String EDIT_USER = "edit user";
    public static final String EDIT_MERCHANT_KYC = "edit merchant kyc";
    public static final String CHANGE_STATUS_SUPPORT_REQUEST = "change status support request";


    public boolean addNewOperatorAction(String author, String eventName, String json, String target) {
        OperatorsAction operatorsAction = new OperatorsAction();
        operatorsAction.setTimestamp(System.currentTimeMillis());
        operatorsAction.setAuthor(author);
        operatorsAction.setEventName(eventName);
        operatorsAction.setJson(json);
        operatorsAction.setTarget(target);
        operatorActionRepository.save(operatorsAction);
        return true;
    }

}
