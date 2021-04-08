package kz.capitalpay.server.login.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.ChangeRolesDTO;
import kz.capitalpay.server.login.dto.CreateNewUserDTO;
import kz.capitalpay.server.login.dto.DeleteUserDTO;
import kz.capitalpay.server.login.dto.EditUserDTO;
import kz.capitalpay.server.login.service.UserListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/userlist")
public class CreateUserController {

    Logger logger = LoggerFactory.getLogger(CreateUserController.class);

    @Autowired
    Gson gson;

    @Autowired
    UserListService userListService;

    @PostMapping("/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO getAllUsers() {
        logger.info("User List");
        return userListService.getAllUsers();
    }

    @PostMapping("/changeroles")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO changeRoles(Principal principal, @RequestBody ChangeRolesDTO request) {
        logger.info(gson.toJson(request));
        return userListService.changeRoles(principal, request);
    }

    @PostMapping("/rolelist")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO roleList(Principal principal) {
        logger.info(gson.toJson(principal));
        return userListService.roleList(principal);
    }

    @PostMapping("/newuser")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO newUser(Principal principal, @Valid @RequestBody CreateNewUserDTO request) {
        logger.info(gson.toJson(request));
        return userListService.newUser(principal, request);
    }


    @PostMapping("/deleteuser")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO deleteUser(Principal principal, @Valid @RequestBody DeleteUserDTO request) {
        logger.info(gson.toJson(request));
        return userListService.deleteUser(principal, request);
    }

    @PostMapping("/edituser")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO editUser(Principal principal, @Valid @RequestBody EditUserDTO request) {
        logger.info(gson.toJson(request));
        return userListService.editUser(principal, request);
    }


}
