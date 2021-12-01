package kz.capitalpay.server.login.controller;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.*;
import kz.capitalpay.server.login.service.UserListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/userlist")
public class CreateUserController {

    Logger logger = LoggerFactory.getLogger(CreateUserController.class);

    @Autowired
    Gson gson;

    @Autowired
    UserListService userListService;

    @Autowired
    SessionRegistry sessionRegistry;

    @PostMapping("/numbersSession")
    public boolean countNumberSessionPerUser(Principal principal) {
        logger.info("principal " + principal.getName());
        return sessionRegistry.getAllSessions(principal, true).size() > 1;
    }

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


    @PostMapping("/activateuser")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO activateUser(@RequestParam Long id) {
        logger.info("activate user with id: {}", id);
        return userListService.activateUser(id);
    }

    @PostMapping("/blockeuser")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO blockUser(@RequestParam Long id) {
        logger.info("block user with id: {}", id);
        return userListService.blockUser(id);
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

    @PostMapping("/one")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_OPERATOR"})
    ResultDTO oneUser(Principal principal, @Valid @RequestBody OneUserDTO request) {
        logger.info(gson.toJson(request));
        return userListService.oneUser(principal, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultDTO handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResultDTO(false, errors, -2);
    }
}
