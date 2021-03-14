package kz.capitalpay.server.login.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.ConfirmCodeCheckRequestDTO;
import kz.capitalpay.server.login.dto.SignUpPhoneRequestDTO;
import kz.capitalpay.server.login.model.PendingEmail;
import kz.capitalpay.server.login.model.PendingPhone;
import kz.capitalpay.server.login.repository.PendingPhoneRepository;
import kz.capitalpay.server.service.SendSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

import static kz.capitalpay.server.constants.ErrorDictionary.*;
import static kz.capitalpay.server.login.service.UserEmailService.CONFIRMED;
import static kz.capitalpay.server.login.service.UserEmailService.PENDING;

@Service
public class UserPhoneService {

    Logger logger = LoggerFactory.getLogger(UserPhoneService.class);

    @Autowired
    Gson gson;

    @Autowired
    UserEmailService userEmailService;

    @Autowired
    PendingPhoneRepository pendingPhoneRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    SendSmsService sendSmsService;

    @Autowired
    ApplicationUserService applicationUserService;

    Random random = new Random(System.currentTimeMillis());

    public ResultDTO savePassword(SignUpPhoneRequestDTO request) {
        try {
            PendingEmail pendingEmail = userEmailService.checkConfirm(request.getCode());
            if (pendingEmail == null) {
                return error102;
            }
            userEmailService.confirm(pendingEmail);
            PendingPhone pendingPhone = pendingPhoneRepository.findTopByPhone(request.getPhone());
            if (pendingPhone != null && !pendingPhone.getStatus().equals(PENDING)) {
                return error103;
            }
            if (pendingPhone == null) {
                pendingPhone = new PendingPhone();
            }
            pendingPhone.setEmail(pendingEmail.getEmail());
            pendingPhone.setPhone(request.getPhone());
            pendingPhone.setTimestamp(System.currentTimeMillis());
            pendingPhone.setConfirmCode(String.valueOf(random.nextInt(999999)));
            pendingPhone.setPasswordHash(bCryptPasswordEncoder.encode(request.getPassword()));
            pendingPhone.setStatus(PENDING);
            pendingPhoneRepository.save(pendingPhone);

            sendSmsService.sendSms(pendingPhone.getConfirmCode(), "CapitalPay confirm code: " + pendingPhone.getConfirmCode());

            return new ResultDTO(true, "SMS sent", 0);
        } catch (Exception e) {
            logger.error("Line number: {} \n{}", e.getStackTrace()[0].getLineNumber(), e.getMessage());
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO confirmPhone(ConfirmCodeCheckRequestDTO request) {
        try {
            PendingPhone pendingPhone = pendingPhoneRepository.findTopByConfirmCodeAndStatus(request.getCode(), PENDING);
            if (pendingPhone == null) {
                return error102;
            }
            pendingPhone.setStatus(CONFIRMED);
            pendingPhoneRepository.save(pendingPhone);
            ResultDTO result = applicationUserService.createNewUser(pendingPhone.getPhone(), pendingPhone.getPasswordHash());
// TODO: Сохранить email в личные данные пользоватея
            return result;
        } catch (Exception e) {
            logger.error("Line number: {} \n{}", e.getStackTrace()[0].getLineNumber(), e.getMessage());
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
