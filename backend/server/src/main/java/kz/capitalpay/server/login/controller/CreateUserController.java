package kz.capitalpay.server.login.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.*;
import kz.capitalpay.server.login.service.UserListService;
import kz.capitalpay.server.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth/userlist")
public class CreateUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserController.class);

    @Autowired
    Gson gson;

    @Autowired
    UserListService userListService;

    @Autowired
    SessionRegistry sessionRegistry;

    @Autowired
    ValidationUtil validationUtil;

    @PostMapping("/numbersSession")
    public boolean countNumberSessionPerUser(Principal principal) {
        LOGGER.info("principal " + principal.getName());
        return sessionRegistry.getAllSessions(principal, true).size() > 1;
    }

    @PostMapping("/list")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO getAllUsers() {
        LOGGER.info("User List");
        return userListService.getAllUsers();
    }

    @PostMapping("/changeroles")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO changeRoles(Principal principal, @RequestBody ChangeRolesDTO request) {
        LOGGER.info(gson.toJson(request));
        return userListService.changeRoles(principal, request);
    }

    @PostMapping("/rolelist")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO roleList(Principal principal) {
        LOGGER.info(gson.toJson(principal));
        return userListService.roleList(principal);
    }

    @PostMapping("/newuser")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO newUser(Principal principal, @Valid @RequestBody CreateNewUserDTO request) {
        LOGGER.info(gson.toJson(request));
        return userListService.newUser(principal, request);
    }


    @PostMapping("/activateuser")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO activateUser(@RequestParam Long id) {
        LOGGER.info("activate user with id: {}", id);
        return userListService.activateUser(id);
    }

    @PostMapping("/blockeuser")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO blockUser(@RequestParam Long id) {
        LOGGER.info("block user with id: {}", id);
        return userListService.blockUser(id);
    }

    @PostMapping("/deleteuser")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO deleteUser(Principal principal, @Valid @RequestBody DeleteUserDTO request) {
        LOGGER.info(gson.toJson(request));
        return userListService.deleteUser(principal, request);
    }

    @PostMapping("/edituser")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO editUser(Principal principal, @Valid @RequestBody EditUserDTO request) {
        LOGGER.info(gson.toJson(request));
        return userListService.editUser(principal, request);
    }

    @PostMapping("/one")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO oneUser(Principal principal, @Valid @RequestBody OneUserDTO request) {
        LOGGER.info(gson.toJson(request));
        return userListService.oneUser(principal, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(MethodArgumentNotValidException ex) {
        return validationUtil.handleValidation(ex);
    }
}
