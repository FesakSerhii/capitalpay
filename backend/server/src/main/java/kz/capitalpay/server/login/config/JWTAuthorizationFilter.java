package kz.capitalpay.server.login.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

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
import java.util.ArrayList;
import java.util.Base64;
import java.util.stream.Collectors;

import static kz.capitalpay.server.login.config.SecurityConstants.HEADER_STRING;
import static kz.capitalpay.server.login.config.SecurityConstants.TOKEN_PREFIX;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(HEADER_STRING);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
//            String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
            String user = JWT.require(getAlgorithm())
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
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
