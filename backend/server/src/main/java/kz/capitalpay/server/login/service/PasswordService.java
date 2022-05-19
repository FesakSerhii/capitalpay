package kz.capitalpay.server.login.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.NewPasswordRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class PasswordService {

    Logger logger = LoggerFactory.getLogger(PasswordService.class);

    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserService applicationUserService;

    public ResultDTO setNewPassword(Principal principal, NewPasswordRequestDTO request) {
        logger.info(gson.toJson(principal));
        String userLogin = principal.getName();
        return applicationUserService.setNewPassword(userLogin,
                request.getOldPassword(),
                request.getNewPassword());
    }
}
