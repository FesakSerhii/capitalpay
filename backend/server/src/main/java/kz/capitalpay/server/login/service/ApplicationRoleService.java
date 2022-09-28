package kz.capitalpay.server.login.service;


import kz.capitalpay.server.login.model.ApplicationRole;
import kz.capitalpay.server.login.repository.ApplicationRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationRoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRoleService.class);

    public static final String USER = "ROLE_USER";
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String OPERATOR = "ROLE_OPERATOR";
    public static final String MERCHANT = "ROLE_MERCHANT";


    @Autowired
    ApplicationRoleRepository applicationRoleRepository;

    public ApplicationRole getRole(String authority) {
        LOGGER.info("Get role");
        ApplicationRole role = applicationRoleRepository.findByAuthority(authority);
        if (role == null) {
            role = new ApplicationRole();
            role.setAuthority(authority);
            applicationRoleRepository.save(role);
        }
        return role;
    }

    public boolean isEmpty() {
        LOGGER.info("Is empty");
        List<ApplicationRole> roleList = applicationRoleRepository.findAll();
        LOGGER.info("Size: {}", roleList.size());
        LOGGER.info("Return {}", roleList.size() == 0);
        return roleList.size() == 0;
    }
}
