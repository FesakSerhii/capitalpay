package kz.capitalpay.server.login.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import kz.capitalpay.server.login.model.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            ApplicationUser cred = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            cred.getUsername(),
                            cred.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername();
        Gson gson = new Gson();
        logger.info(gson.toJson(authResult.getAuthorities()));

        Set<String> roleSet = authResult.getAuthorities().stream()
                .map(r -> r.getAuthority()).collect(Collectors.toSet());
        List<String> roleList = new ArrayList<>(roleSet);
        String[] roles = roleList.toArray(new String[roleList.size()]);
        System.out.println(roleList.size());
        System.out.println(authResult.getAuthorities().size());
        System.out.println(roles.length);
        String token = JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withArrayClaim("authorities", roles)
                .withJWTId(UUID.randomUUID().toString())
                .sign(getAlgorithm());
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }


    private Algorithm getAlgorithm() {

        final InputStream inputStreamPriv = getClass().getClassLoader()
                .getResourceAsStream("rsa/privatekey.pem");

        String privateKeyContent = new BufferedReader(
                new InputStreamReader(inputStreamPriv, StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));

        final InputStream inputStreamPub = getClass().getClassLoader()
                .getResourceAsStream("rsa/publickey.pem");

        String publicKeyContent = new BufferedReader(
                new InputStreamReader(inputStreamPub, StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));


        publicKeyContent = publicKeyContent.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
        privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");

        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec keySpecX509pub = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
            PKCS8EncodedKeySpec keySpecX509priv = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
            RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509pub);
            RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(keySpecX509priv);


            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            return algorithm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
