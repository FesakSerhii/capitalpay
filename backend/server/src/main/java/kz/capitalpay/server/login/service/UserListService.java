package kz.capitalpay.server.login.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.login.dto.*;
import kz.capitalpay.server.login.mapper.ApplicationUserMapper;
import kz.capitalpay.server.login.model.ApplicationRole;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.model.DeletedApplicationUser;
import kz.capitalpay.server.login.repository.ApplicationUserRepository;
import kz.capitalpay.server.login.repository.DeletedApplicationUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static kz.capitalpay.server.constants.ErrorDictionary.error106;
import static kz.capitalpay.server.constants.ErrorDictionary.error107;
import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.*;
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

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    DeletedApplicationUserRepository deletedApplicationUserRepository;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;

    @Autowired
    ApplicationUserMapper applicationUserMapper;


    public ResultDTO getAllUsers() {
        try {
            List<ApplicationUser> applicationUserList = applicationUserRepository.findAll();
            applicationUserList = removePasswords(applicationUserList);
            List<ApplicationUserResponseDto> dtos = applicationUserList.stream().map(applicationUserMapper::convertToResponseDto).collect(Collectors.toList());
            return new ResultDTO(true, dtos, 0);
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
//            if (applicationUser == null) {
//                return error106;
//            }

            ApplicationUser admin = applicationUserRepository.findByUsername(principal.getName());
            systemEventsLogsService.addNewOperatorAction(principal.getName(),
                    CHANGE_ROLE, gson.toJson(request), applicationUser.getId().toString());

            if (admin.getRoles().contains(applicationRoleService.getRole(ADMIN))) {
                logger.info("Admin!");
                applicationUser.setRoles(roleListFromStringList(request.getRoleList()));
                applicationUserRepository.save(applicationUser);
            } else if (admin.getRoles().contains(applicationRoleService.getRole(OPERATOR))) {
                logger.info("Operator!");
//                if (request.getRoleList().contains(OPERATOR) || request.getRoleList().contains(ADMIN)) {
//                    return error107;
//                }
                Set<ApplicationRole> newRoles = roleListFromStringList(request.getRoleList());
                Set<ApplicationRole> needToRemove = applicationUser.getRoles();
                needToRemove.removeAll(newRoles);
//                if (needToRemove.contains(applicationRoleService.getRole(OPERATOR)) ||
//                        needToRemove.contains(applicationRoleService.getRole(ADMIN))) {
//                    return error107;
//                }
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
        if (admin == null) {
            logger.error(gson.toJson(principal));
            logger.error("Admin: {}", admin);
            return error106;
        }
        Set<ApplicationRole> applicationRoles = new HashSet<>();
        if (admin.getRoles() == null) {
            logger.error("Admin role list: {}", admin.getRoles());
        } else {
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
        }
        return new ResultDTO(true, applicationRoles, 0);
    }

    public ResultDTO newUser(Principal principal, CreateNewUserDTO request) {
        try {
            ResultDTO result = applicationUserService.createNewUser(request.getPhone(), bCryptPasswordEncoder.encode(request.getPassword()));
            if (!result.isResult()) {
                return result;
            }
            logger.info(gson.toJson(result));
            ApplicationUser applicationUser = applicationUserRepository.findByUsername((String) result.getData());
            applicationUser.setEmail(request.getEmail());
            applicationUser.setTimestamp(System.currentTimeMillis());
            applicationUser.setActive(false);
            ApplicationUser admin = applicationUserRepository.findByUsername(principal.getName());

            if (!admin.getRoles().contains(applicationRoleService.getRole(ADMIN))) {
                if (request.getRoleList().contains(OPERATOR) || request.getRoleList().contains(ADMIN)) {
                    return error107;
                }
            }
            applicationUser.setRoles(roleListFromStringList(request.getRoleList()));
            applicationUserRepository.save(applicationUser);
            ApplicationUser resultUser = maskPassword(applicationUser);

            request.setPassword(null);
            systemEventsLogsService.addNewOperatorAction(principal.getName(),
                    CREATE_USER, gson.toJson(request), applicationUser.getId().toString());
            return new ResultDTO(true, resultUser, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private ApplicationUser maskPassword(ApplicationUser applicationUser) {
        ApplicationUser result = new ApplicationUser();
        result.setUsername(applicationUser.getUsername());
        result.setId(applicationUser.getId());
        result.setEmail(applicationUser.getEmail());
        result.setRoles(applicationUser.getRoles());
        result.setBlocked(applicationUser.isBlocked());
        result.setActive(applicationUser.isActive());
        return result;
    }

    public ResultDTO deleteUser(Principal principal, DeleteUserDTO request) {

        try {

            ApplicationUser applicationUser = applicationUserService.getUserById(request.getUserId());
            if (applicationUser == null) {
                return error106;
            }
            ApplicationUser admin = applicationUserRepository.findByUsername(principal.getName());
            if (!admin.getRoles().contains(applicationRoleService.getRole(ADMIN))) {
                Set<ApplicationRole> roleSet = applicationUser.getRoles();
                if (roleSet.contains(applicationRoleService.getRole(OPERATOR)) ||
                        roleSet.contains(applicationRoleService.getRole(OPERATOR))) {
                    return error107;
                }
            }

            DeletedApplicationUser deleted = new DeletedApplicationUser(applicationUser);
            deletedApplicationUserRepository.save(deleted);
            applicationUserRepository.delete(applicationUser);

            systemEventsLogsService.addNewOperatorAction(principal.getName(),
                    DELETE_USER, gson.toJson(request), applicationUser.getId().toString());

            return new ResultDTO(true, request.getUserId(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }

    }


    public ResultDTO editUser(Principal principal, EditUserDTO request) {
        try {
            ApplicationUser applicationUser = applicationUserService.getUserById(request.getId());
            if (applicationUser == null) {
                return error106;
            }
            ApplicationUser admin = applicationUserRepository.findByUsername(principal.getName());
            if (!admin.getRoles().contains(applicationRoleService.getRole(ADMIN))) {
                Set<ApplicationRole> roleSet = applicationUser.getRoles();
                if (roleSet.contains(applicationRoleService.getRole(OPERATOR)) ||
                        roleSet.contains(applicationRoleService.getRole(OPERATOR))) {
                    return error107;
                }
            }

            applicationUser.setEmail(request.getEmail());
            applicationUser.setUsername(request.getPhone());
            applicationUser.setActive(request.isActive());
            applicationUser.setBlocked(request.isBlocked());
            if (request.getRealname() != null) {
                applicationUser.setRealname(request.getRealname());
            }
            if (request.getPassword() != null) {
                applicationUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
            }

            applicationUserRepository.save(applicationUser);
            ApplicationUser resultUser = maskPassword(applicationUser);

            request.setPassword(null);
            systemEventsLogsService.addNewOperatorAction(principal.getName(),
                    EDIT_USER, gson.toJson(request), applicationUser.getId().toString());
            return new ResultDTO(true, resultUser, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO oneUser(Principal principal, OneUserDTO request) {
        try {
            ApplicationUser applicationUser = applicationUserService.getUserById(request.getId());
            if (applicationUser == null) {
                return error106;
            }
            ApplicationUser resultUser = maskPassword(applicationUser);
            systemEventsLogsService.addNewOperatorAction(principal.getName(),
                    EDIT_USER, gson.toJson(request), applicationUser.getId().toString());
            return new ResultDTO(true, resultUser, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO activateUser(Long id) {
        ApplicationUser user = applicationUserRepository.findById(id).orElse(null);
        if (Objects.isNull(user)) {
            return error106;
        }
        user.setActive(true);
        user.setBlocked(false);
        applicationUserRepository.save(user);
        return new ResultDTO(true, "", 0);
    }

    public ResultDTO blockUser(Long id) {
        ApplicationUser user = applicationUserRepository.findById(id).orElse(null);
        if (Objects.isNull(user)) {
            return error106;
        }
        user.setActive(false);
        user.setBlocked(true);
        applicationUserRepository.save(user);
        return new ResultDTO(true, "", 0);
    }
}
