package kz.capitalpay.server.service;

import kz.capitalpay.server.login.model.PendingPhone;
import kz.capitalpay.server.login.repository.PendingPhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendSmsService {

    @Autowired
    SendEmailService sendEmailService;

    @Autowired
    PendingPhoneRepository pendingPhoneRepository;


    public boolean sendSms(String phone, String text) {
        if (!phone.contains("+7")) {
            PendingPhone pendingPhone = pendingPhoneRepository.findTopByPhone(phone);
            sendEmailService.sendMail(pendingPhone.getEmail(),
                    "SMS Capital Pay", text);
        }
        return true;
    }
}
