package kz.capitalpay.server.login.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.model.ApplicationRole;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.repository.ApplicationUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static kz.capitalpay.server.constants.ErrorDictionary.error104;
import static kz.capitalpay.server.login.service.ApplicationRoleService.ADMIN;
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

            Set<ApplicationRole> roles = new HashSet<>();
            if (applicationRoleService.isEmpty()) {
                logger.info("Add admin");
                roles.add(applicationRoleService.getRole(ADMIN));
            }
            roles.add(applicationRoleService.getRole(USER));
            logger.info(gson.toJson(roles));
            applicationUser.setRoles(roles);
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

    public ResultDTO createNewUser(String username, String passwordHash) {
        ApplicationUser user = applicationUserRepository.findByUsername(username);
        if (user != null && user.getUsername().equals(username)) {
            return error104;
        }
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUsername(username);
        applicationUser.setPassword(passwordHash);
        applicationUser.setRoles(Collections.singleton(applicationRoleService.getRole(USER)));
        applicationUserRepository.save(applicationUser);
        return new ResultDTO(true, String.format("User %s created", username), 0);
    }
}

