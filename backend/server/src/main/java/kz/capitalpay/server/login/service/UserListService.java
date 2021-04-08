package kz.capitalpay.server.login.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.ChangeRolesDTO;
import kz.capitalpay.server.login.model.ApplicationRole;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.repository.ApplicationUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static kz.capitalpay.server.constants.ErrorDictionary.error106;
import static kz.capitalpay.server.constants.ErrorDictionary.error107;
import static kz.capitalpay.server.login.service.ApplicationRoleService.*;

@Service
public class UserListService {

    Logger logger = LoggerFactory.getLogger(UserListService.class);

    @Autowired
    Gson gson;

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Autowired
    ApplicationUserService applicationUserService;

    @Autowired
    ApplicationRoleService applicationRoleService;

    public ResultDTO getAllUsers() {
        try {
            List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
            applicationUserList = removePasswords(applicationUserList);
            return new ResultDTO(true, applicationUserList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }


    List<ApplicationUser> removePasswords(List<ApplicationUser> applicationUserList) {
        applicationUserList.forEach(applicationUser -> applicationUser.setPassword(null));
        return applicationUserList;
    }

    public ResultDTO changeRoles(Principal principal, ChangeRolesDTO request) {
        try {
            ApplicationUser applicationUser = applicationUserService.getUserById(request.getUserId());
            if (applicationUser == null) {
                return error106;
            }

            ApplicationUser admin = applicationUserRepository.findByUsername(principal.getName());

            if (admin.getRoles().contains(applicationRoleService.getRole(ADMIN))) {
                logger.info("Admin!");


                applicationUser.setRoles(roleListFromStringList(request.getRoleList()));
                applicationUserRepository.save(applicationUser);

            } else if (admin.getRoles().contains(applicationRoleService.getRole(OPERATOR))) {
                logger.info("Operator!");

                if (request.getRoleList().contains(OPERATOR) || request.getRoleList().contains(ADMIN)) {
                    return error107;
                }

                Set<ApplicationRole> newRoles = roleListFromStringList(request.getRoleList());

                Set<ApplicationRole> needToRemove = applicationUser.getRoles();
                needToRemove.removeAll(newRoles);

                if (needToRemove.contains(applicationRoleService.getRole(OPERATOR)) ||
                        needToRemove.contains(applicationRoleService.getRole(ADMIN))) {
                    return error107;
                }

                applicationUser.setRoles(newRoles);
                applicationUserRepository.save(applicationUser);
            }


            return new ResultDTO(true, request.getRoleList(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }

    private Set<ApplicationRole> roleListFromStringList(List<String> roleList) {
        Set<ApplicationRole> roleSet = new HashSet<>();
        for (String role : roleList) {
            roleSet.add(applicationRoleService.getRole(role));
        }
        return roleSet;
    }

    public ResultDTO roleList(Principal principal) {

        ApplicationUser admin = applicationUserRepository.findByUsername(principal.getName());
        Set<ApplicationRole> applicationRoles = new HashSet<>();
        if (admin.getRoles().contains(applicationRoleService.getRole(ADMIN))) {

            applicationRoles.add(applicationRoleService.getRole(USER));
            applicationRoles.add(applicationRoleService.getRole(MERCHANT));
            applicationRoles.add(applicationRoleService.getRole(OPERATOR));
            applicationRoles.add(applicationRoleService.getRole(ADMIN));

        } else if (admin.getRoles().contains(applicationRoleService.getRole(OPERATOR))) {

            applicationRoles.add(applicationRoleService.getRole(USER));
            applicationRoles.add(applicationRoleService.getRole(MERCHANT));
            applicationRoles.add(applicationRoleService.getRole(OPERATOR));
            applicationRoles.add(applicationRoleService.getRole(ADMIN));
        }
        return new ResultDTO(true, applicationRoles, 0);
    }
}
