package kz.capitalpay.server.login.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.SignUpEmailRequestDTO;
import kz.capitalpay.server.login.model.PendingEmail;
import kz.capitalpay.server.login.repository.PendingEmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static kz.capitalpay.server.constants.ErrorDictionary.error101;

@Service
public class UserEmailService {
    Logger logger = LoggerFactory.getLogger(UserEmailService.class);

    @Autowired
    Gson gson;

    @Autowired
    PendingEmailRepository pendingEmailRepository;

    public static final String PENDING = "PENDING";
    public static final String CONFIRMED = "CONFIRMED";

    public ResultDTO setNewEmail(SignUpEmailRequestDTO request) {
        try {
            PendingEmail pendingEmail = pendingEmailRepository.findByEmail(request.getEmail());
            if (pendingEmail != null && !pendingEmail.getStatus().equals(PENDING)) {
                return error101;
            }
            pendingEmail.setEmail(request.getEmail());
            pendingEmail.setStatus(PENDING);
            pendingEmail.setConfirmCode(UUID.randomUUID().toString());
            pendingEmail.setTimestamp(System.currentTimeMillis());
            pendingEmailRepository.save(pendingEmail);

            return new ResultDTO(true, request, 0);
        } catch (Exception e) {
            logger.error("Line number: {} \n{}", e.getStackTrace()[0].getLineNumber(), e.getMessage());
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
