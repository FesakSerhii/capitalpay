package kz.capitalpay.server.login.service;

import com.auth0.jwt.JWT;
import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.ApplicationUserDTO;
import kz.capitalpay.server.login.dto.TwoFactorAuthDTO;
import kz.capitalpay.server.login.model.ApplicationRole;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.repository.ApplicationUserRepository;
import kz.capitalpay.server.service.SendSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import static kz.capitalpay.server.constants.ErrorDictionary.USER_ALREADY_EXISTS;
import static kz.capitalpay.server.constants.ErrorDictionary.OLD_PASSWORD_DOES_NOT_MATCH;
import static kz.capitalpay.server.login.config.SecurityConstants.HEADER_STRING;
import static kz.capitalpay.server.login.config.SecurityConstants.TOKEN_PREFIX;
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

    @Autowired
    TwoFactorService twoFactorService;

    @Autowired
    SendSmsService sendSmsService;

    @Autowired
    TrustIpService trustIpService;

    Random random = new Random();

    public String findSmsCode(ApplicationUser applicationUser) {
        return twoFactorService.findCodeFromSms(applicationUser);
    }

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
        return user != null && user.getUsername().equals(applicationUser.getUsername());
    }

    public ResultDTO createNewUser(String username, String passwordHash) {
        ApplicationUser user = applicationUserRepository.findByUsername(username);
        if (user != null && user.getUsername().equals(username)) {
            return USER_ALREADY_EXISTS;
        }
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUsername(username);
        applicationUser.setPassword(passwordHash);

        Set<ApplicationRole> roles = new HashSet<>();
        if (applicationRoleService.isEmpty()) {
            logger.info("Add admin");
            roles.add(applicationRoleService.getRole(ADMIN));
        }
        roles.add(applicationRoleService.getRole(USER));
        logger.info(gson.toJson(roles));
        applicationUser.setRoles(roles);
        applicationUserRepository.save(applicationUser);
        return new ResultDTO(true, username, 0);
    }

    public ResultDTO setNewPassword(String userLogin, String oldPassword, String newPassword) {
        ApplicationUser user = applicationUserRepository.findByUsername(userLogin);
        if (bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            applicationUserRepository.save(user);
            return new ResultDTO(true, user.getUsername(), 0);
        } else {
            return OLD_PASSWORD_DOES_NOT_MATCH;
        }

    }


    public boolean requireTwoFactorAuth(String username) {
        ApplicationUser user = applicationUserRepository.findByUsername(username);
        return twoFactorService.isRequired(user);
    }

    public void sendTwoFactorSms(String username) {

        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        if (applicationUser != null) {

            twoFactorService.sendSms(applicationUser);
        }

    }

    public void checkTwoFactorSms(ApplicationUserDTO cred) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(cred.getUsername());
        twoFactorService.checkCode(applicationUser.getId(), cred.getSms());
    }

    public boolean smsNeedCheck(String username) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        return twoFactorService.smsNeedCheck(applicationUser.getId());
    }

    public boolean smsCheckResult(String username) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        return twoFactorService.checkSmsCode(applicationUser.getId());
    }

    public ResultDTO twoFactorAuth(Principal principal, TwoFactorAuthDTO request) {
        try {
            ApplicationUser applicationUser = applicationUserRepository.findByUsername(principal.getName());
            if (request.isEnable()) {
                twoFactorService.setTwoFactor(applicationUser.getId());
            } else {
                twoFactorService.removeTwoFactorAuth(applicationUser.getId());
            }

            return new ResultDTO(true, "Two Factor Set", 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ApplicationUser getUserById(Long userId) {
        return applicationUserRepository.findById(userId).orElse(null);
    }

    public ApplicationUser getUserByLogin(String name) {
        return applicationUserRepository.findByUsername(name);
    }

    public String validIpAddress(HttpServletRequest request, String username) {
        String ip = getIpAddress(request);
        String userAgent = getUserAgent(request);
        logger.info("IP: {}", ip);
        logger.info("User-Agent: {}", userAgent);

        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        if (Objects.isNull(applicationUser)) {
            return null;
        }
        logger.info(" archer id " + applicationUser.getId() + " userName " + username);
        return trustIpService.validIpAddress(applicationUser.getId(), ip);
    }


    private String getUserAgent(HttpServletRequest servletRequest) {
        return servletRequest.getHeader("User-Agent");
    }

    private String getIpAddress(HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = servletRequest.getRemoteAddr();
        }
        return ipAddress;
    }

    public void setTrustIp(String ip, ApplicationUserDTO cred) {
        if (cred.isTrustIp()) {
            ApplicationUser applicationUser = applicationUserRepository.findByUsername(cred.getUsername());
            trustIpService.addTrustIp(applicationUser.getId(), ip);
        }
    }

    public Long getMerchantIdFromToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "");
        return JWT.decode(token).getClaim("merchantId").asLong();
    }
}

