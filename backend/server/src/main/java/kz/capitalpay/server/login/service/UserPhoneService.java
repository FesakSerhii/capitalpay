package kz.capitalpay.server.login.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.ConfirmCodeCheckRequestDTO;
import kz.capitalpay.server.login.dto.SignUpPhoneRequestDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.model.PendingEmail;
import kz.capitalpay.server.login.model.PendingPhone;
import kz.capitalpay.server.login.repository.ApplicationUserRepository;
import kz.capitalpay.server.login.repository.PendingPhoneRepository;
import kz.capitalpay.server.service.SendSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

import static kz.capitalpay.server.constants.ErrorDictionary.CONFIRM_CODE_NOT_FOUND;
import static kz.capitalpay.server.constants.ErrorDictionary.PHONE_USED;
import static kz.capitalpay.server.login.service.UserEmailService.CONFIRMED;
import static kz.capitalpay.server.login.service.UserEmailService.PENDING;

@Service
public class UserPhoneService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPhoneService.class);

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

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    private final Random random = new Random(System.currentTimeMillis());

    public ResultDTO savePassword(SignUpPhoneRequestDTO request) {
        try {
            PendingEmail pendingEmail = userEmailService.checkConfirm(request.getCode());
            if (pendingEmail == null) {
                return CONFIRM_CODE_NOT_FOUND;
            }
            userEmailService.confirm(pendingEmail);
            PendingPhone pendingPhone = pendingPhoneRepository.findTopByPhone(request.getPhone());
            if (pendingPhone != null && !pendingPhone.getStatus().equals(PENDING)) {
                return PHONE_USED;
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
            sendSmsService.sendSms(pendingPhone.getPhone(), "CapitalPay confirm code: " + pendingPhone.getConfirmCode());
            return new ResultDTO(true, "SMS sent", 0);
        } catch (Exception e) {
            LOGGER.error("Line number: {} \n{}", e.getStackTrace()[0].getLineNumber(), e.getMessage());
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO confirmPhone(ConfirmCodeCheckRequestDTO request) {
        try {
            PendingPhone pendingPhone = pendingPhoneRepository.findTopByConfirmCodeAndStatus(request.getCode(), PENDING);
            if (pendingPhone == null) {
                return CONFIRM_CODE_NOT_FOUND;
            }
            pendingPhone.setStatus(CONFIRMED);
            pendingPhoneRepository.save(pendingPhone);
            ResultDTO result = applicationUserService.createNewUser(pendingPhone.getPhone(), pendingPhone.getPasswordHash());
            if (!result.isResult()) {
                return result;
            }
            ApplicationUser applicationUser = applicationUserRepository.findByUsername((String) result.getData());
            applicationUser.setEmail(pendingPhone.getEmail());
            applicationUserRepository.save(applicationUser);
            return result;
        } catch (Exception e) {
            LOGGER.error("Line number: {} \n{}", e.getStackTrace()[0].getLineNumber(), e.getMessage());
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
