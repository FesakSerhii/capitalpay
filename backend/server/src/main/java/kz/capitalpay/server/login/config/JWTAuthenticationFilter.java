package kz.capitalpay.server.login.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import kz.capitalpay.server.ContextProvider;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.login.dto.ApplicationUserDTO;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

import static kz.capitalpay.server.login.config.SecurityConstants.*;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private final AuthenticationManager authenticationManager;
    private final ApplicationUserService applicationUserService = ContextProvider.getBean(ApplicationUserService.class);

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LOGGER.info("step3");
        ApplicationUserDTO cred = null;
        try {
            cred = new ObjectMapper().readValue(request.getInputStream(), ApplicationUserDTO.class);
            LOGGER.info("cred " + cred);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Objects.isNull(cred) || Objects.isNull(cred.getPassword()) || Objects.isNull(cred.getUsername()) || cred.getPassword().trim().isEmpty() || cred.getUsername().trim().isEmpty()) {
            throw new AuthenticationException("Fields are empty") {
            };
        }

        LOGGER.info("archer before " + cred.getUsername());
        String ip = applicationUserService.validIpAddress(request, cred.getUsername());
        LOGGER.info("ip " + ip);
        if (ip == null) {
            throw new AuthenticationException("Authentication failed") {
            };
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(cred.getUsername(), cred.getPassword(), new ArrayList<>());
        LOGGER.info("token from auth " + token);

        if (applicationUserService.requireTwoFactorAuth(cred.getUsername())) {
            if (cred.getSms() == null) {
                applicationUserService.sendTwoFactorSms(cred.getUsername());
            } else {
                applicationUserService.checkTwoFactorSms(cred);
            }
        }

        Authentication authentication = authenticationManager.authenticate(token);
        LOGGER.info("authentication " + authentication.getPrincipal().toString());
        applicationUserService.setTrustIp(ip, cred);
        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername();
        Gson gson = new Gson();
        String[] roles = new String[authResult.getAuthorities().size()];

        Set<String> roleSet = authResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        roles = roleSet.toArray(roles);

        ApplicationUser applicationUser = applicationUserService.getUserByLogin(username);
        LOGGER.info("application User" + applicationUser);

        if (applicationUserService.requireTwoFactorAuth(username)) {
            if (applicationUserService.smsNeedCheck(username)) {
                if (applicationUserService.smsCheckResult(username)) {
                    LOGGER.info("step4");
                    String token = JWT.create().withSubject(username).withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).withArrayClaim("authorities", roles).withClaim("merchantId", applicationUser.getId()).withJWTId(UUID.randomUUID().toString()).sign(getAlgorithm());
                    response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
                    LOGGER.info("token step4" + token);

                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(gson.toJson(new ResultDTO(true, username, 0)));
                } else {
                    LOGGER.info("step5");
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                    response.getWriter().write(gson.toJson(new ResultDTO(true, "SMS sent", 0)));
                    //TODO:костыль для тестирования двухфакторной аутентификации
                    response.getWriter().write(gson.toJson(new ResultDTO(true, "SMS sent", 0, applicationUserService.findSmsCode(applicationUser))));
                }
            } else {
                LOGGER.info("step6");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                response.getWriter().write(gson.toJson(new ResultDTO(true, "SMS sent", 0)));
                //TODO:костыль для тестирования двухфакторной аутентификации
                response.getWriter().write(gson.toJson(new ResultDTO(true, "SMS sent", 0, applicationUserService.findSmsCode(applicationUser))));
            }
        } else {
            LOGGER.info("step7");
            String token = JWT.create().withSubject(username).withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).withArrayClaim("authorities", roles).withClaim("merchantId", applicationUser.getId()).withJWTId(UUID.randomUUID().toString()).sign(getAlgorithm());
            LOGGER.info("ste7 token " + token);
            response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(gson.toJson(new ResultDTO(true, roles, 0)));
        }
    }

    private Algorithm getAlgorithm() {
        final InputStream inputStreamPriv = getClass().getClassLoader().getResourceAsStream("rsa/privatekey.pem");

        String privateKeyContent = new BufferedReader(new InputStreamReader(inputStreamPriv, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

        final InputStream inputStreamPub = getClass().getClassLoader().getResourceAsStream("rsa/publickey.pem");

        String publicKeyContent = new BufferedReader(new InputStreamReader(inputStreamPub, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));


        publicKeyContent = publicKeyContent.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
        privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");

        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509pub = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
            PKCS8EncodedKeySpec keySpecX509priv = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
            RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509pub);
            RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(keySpecX509priv);
            return Algorithm.RSA256(publicKey, privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
