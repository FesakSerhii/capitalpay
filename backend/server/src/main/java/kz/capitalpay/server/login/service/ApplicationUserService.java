package kz.capitalpay.server.login.service;

import com.google.gson.Gson;

import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.repository.ApplicationUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static kz.capitalpay.server.login.service.ApplicationRoleService.USER;

@Service
public class ApplicationUserService {

    Logger logger = LoggerFactory.getLogger(ApplicationUserService.class);
    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Autowired
    ApplicationRoleService applicationRoleService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    public void signUp(ApplicationUser applicationUser) {
        if (!existsUser(applicationUser)) {
            applicationUser.setPassword(bCryptPasswordEncoder.encode(applicationUser.getPassword()));
            applicationUser.setRoles(Collections.singleton(applicationRoleService.getRole(USER)));
            applicationUserRepository.save(applicationUser);
        } else {
            throw new IllegalArgumentException("User " + applicationUser.getUsername() + " exists");
        }

        logger.info(gson.toJson(applicationUser));
    }

    private boolean existsUser(ApplicationUser applicationUser) {
        ApplicationUser user = applicationUserRepository.findByUsername(applicationUser.getUsername());
        if (user != null && user.getUsername().equals(applicationUser.getUsername())) {
            return true;
        }
        return false;
    }
}
