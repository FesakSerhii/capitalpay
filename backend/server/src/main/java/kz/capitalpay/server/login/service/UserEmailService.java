package kz.capitalpay.server.login.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.ConfirmCodeCheckRequestDTO;
import kz.capitalpay.server.login.dto.SignUpEmailRequestDTO;
import kz.capitalpay.server.login.model.PendingEmail;
import kz.capitalpay.server.login.repository.PendingEmailRepository;
import kz.capitalpay.server.service.SendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static kz.capitalpay.server.constants.ErrorDictionary.CONFIRM_CODE_NOT_FOUND;
import static kz.capitalpay.server.constants.ErrorDictionary.EMAIL_USED;

@Service
public class UserEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEmailService.class);

    @Autowired
    Gson gson;

    @Autowired
    PendingEmailRepository pendingEmailRepository;

    @Autowired
    SendEmailService sendEmailService;

    public static final String PENDING = "PENDING";
    public static final String CONFIRMED = "CONFIRMED";

    public ResultDTO setNewEmail(SignUpEmailRequestDTO request) {
        try {
            PendingEmail pendingEmail = pendingEmailRepository.findByEmail(request.getEmail());
            if (pendingEmail != null && !pendingEmail.getStatus().equals(PENDING)) {
                return EMAIL_USED;
            }
            if (pendingEmail == null) {
                pendingEmail = new PendingEmail();
            }
            pendingEmail.setEmail(request.getEmail());
            pendingEmail.setStatus(PENDING);
            pendingEmail.setConfirmCode(UUID.randomUUID().toString());
            pendingEmail.setTimestamp(System.currentTimeMillis());
            pendingEmailRepository.save(pendingEmail);

            sendEmailService.sendMail(request.getEmail(), "CapitalPay: Confirm email", "Пожалуйста подтвердите ваш электронный адрес по ссылке:  https://capitalpay.kz/confirm?code=" + pendingEmail.getConfirmCode());

            return new ResultDTO(true, request, 0);
        } catch (Exception e) {
            LOGGER.error("Line number: {} \n{}", e.getStackTrace()[0].getLineNumber(), e.getMessage());
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    public ResultDTO checkConfirm(ConfirmCodeCheckRequestDTO request) {
        PendingEmail pendingEmail = checkConfirm(request.getCode());
        if (pendingEmail != null) {
            return new ResultDTO(true, pendingEmail, 0);
        } else {
            return CONFIRM_CODE_NOT_FOUND;
        }
    }

    public PendingEmail checkConfirm(String code) {
        PendingEmail pendingEmail = pendingEmailRepository.findByConfirmCode(code);
        if (pendingEmail != null && pendingEmail.getStatus().equals(PENDING)) {
            return pendingEmail;
        } else {
            return null;
        }
    }

    public void confirm(PendingEmail pendingEmail) {
        pendingEmail.setStatus(CONFIRMED);
        pendingEmailRepository.save(pendingEmail);
    }
}
