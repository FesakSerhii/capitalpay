package kz.capitalpay.server.login.service;

import com.google.gson.Gson;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.model.TwoFactorAuth;
import kz.capitalpay.server.login.repository.TwoFactorAuthRepository;
import kz.capitalpay.server.service.SendSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Random;

@Service
public class TwoFactorService {

    Logger logger = LoggerFactory.getLogger(TwoFactorService.class);

    Random random = new Random();

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    TwoFactorAuthRepository twoFactorAuthRepository;

    @Autowired
    SendSmsService sendSmsService;

    @Autowired
    Gson gson;

    public String findCodeFromSms(ApplicationUser user) {
        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findById(user.getId()).orElse(null);
        return twoFactorAuth == null ? "" : twoFactorAuth.getSmscode();
    }

    boolean setTwoFactor(Long userId) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setUserId(userId);
        twoFactorAuthRepository.save(twoFactorAuth);
        return true;
    }

    boolean removeTwoFactorAuth(Long userId) {
        twoFactorAuthRepository.deleteById(userId);
        return true;
    }


    public boolean isRequired(ApplicationUser user) {
        if (user != null) {
            TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findById(user.getId()).orElse(null);
            return twoFactorAuth != null;
        } else {
            logger.error("User: {}", gson.toJson(user));
            return false;
        }
    }

    public boolean isAvailableTwoFactorAuth(Principal principal) {
        ApplicationUser applicationUser = applicationUserService.getUserByLogin(principal.getName());
        return isRequired(applicationUser);
    }

    public void sendSms(ApplicationUser user) {
        String code = String.valueOf(random.nextInt(999999));
        sendSmsService.sendSms(user.getUsername(), "Code: " + code);

        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findById(user.getId()).orElse(null);

        if (twoFactorAuth != null) {
            twoFactorAuth.setSmscode(code);
            twoFactorAuthRepository.save(twoFactorAuth);
        }
    }

    public void checkCode(Long id, String sms) {
        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findById(id).orElse(null);
        if (twoFactorAuth != null) {
            twoFactorAuth.setCheckSms(sms);
            twoFactorAuthRepository.save(twoFactorAuth);
            if (twoFactorAuth.getCheckSms() != null && !twoFactorAuth.getSmscode().equals(twoFactorAuth.getCheckSms())) {
                logger.error("SMS: " + twoFactorAuth.getSmscode() + " != " + twoFactorAuth.getCheckSms());
            }
        }
    }

    public boolean smsNeedCheck(Long id) {
        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findById(id).orElse(null);
        if (twoFactorAuth != null) {
            return twoFactorAuth.getCheckSms() != null;
        }
        return false;
    }

    public boolean checkSmsCode(Long id) {
        TwoFactorAuth twoFactorAuth = twoFactorAuthRepository.findById(id).orElse(null);
        if (twoFactorAuth != null) {
            if (twoFactorAuth.getCheckSms() != null && !twoFactorAuth.getSmscode().equals(twoFactorAuth.getCheckSms())) {
                logger.error("SMS: " + twoFactorAuth.getSmscode() + " != " + twoFactorAuth.getCheckSms());
                return false;
            } else {
                twoFactorAuth.setCheckSms(null);
                twoFactorAuth.setSmscode(null);
                twoFactorAuthRepository.save(twoFactorAuth);
                return true;
            }
        }
        return false;
    }
}
