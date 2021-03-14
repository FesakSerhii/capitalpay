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
/*

        String publicKeyContent = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3RlFMRLfb0n7pve08qgX\n" +
                "Xv5ajtREyGknir8Cbwwsq/FWwWjQDFo3OZNO3zpPPfCpk0L/VUwkWWro5Y6wFBzC\n" +
                "bfHKfir2Ibuqy3oM87v2p/jdMmPKxx6UtHQ3vrrSAJgZRw7bhMtURk4xy0y/N3kN\n" +
                "uARLupQootizCRef01cNZTWz0mY28MrJrpGVpNxr/APcan5bJum9VI1z9dpBL+W+\n" +
                "JuNL7NasUS79aneP3L4V/3nckntxhON52adaSAyVLuuUmd/NbIgYLvSkuI7pZ+/U\n" +
                "7/k8XkAnTjxbnRsezssRMgJYuCNthQ96h6vdxViurQQhMz2y/jWpncYtBSPsbwtE\n" +
                "GQIDAQAB\n" +
                "-----END PUBLIC KEY-----\n";

        String privateKeyContent = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEowIBAAKCAQEA3RlFMRLfb0n7pve08qgXXv5ajtREyGknir8Cbwwsq/FWwWjQ\n" +
                "DFo3OZNO3zpPPfCpk0L/VUwkWWro5Y6wFBzCbfHKfir2Ibuqy3oM87v2p/jdMmPK\n" +
                "xx6UtHQ3vrrSAJgZRw7bhMtURk4xy0y/N3kNuARLupQootizCRef01cNZTWz0mY2\n" +
                "8MrJrpGVpNxr/APcan5bJum9VI1z9dpBL+W+JuNL7NasUS79aneP3L4V/3nckntx\n" +
                "hON52adaSAyVLuuUmd/NbIgYLvSkuI7pZ+/U7/k8XkAnTjxbnRsezssRMgJYuCNt\n" +
                "hQ96h6vdxViurQQhMz2y/jWpncYtBSPsbwtEGQIDAQABAoIBADNL2p7BFy+1xXn+\n" +
                "fG/UCeATOSVsIC0zIGZzXHgxJegajRtAWwySnb9w6tqlp57iSCWPZLgAe2p3gGNK\n" +
                "EJYJ+h4R+/37r7Ex3jVpMroVwOtFtOQs/O3nrW6UjB5nL7PNlgfDbbDIj9vj5m+Z\n" +
                "db/ECSfJb1xxZWAq0JbqcT1SNBxnw8X18mJzwaG5IveERUSjChNNyR1bdZQdXhOY\n" +
                "Yl2eFlOQNQzsMYfozkbakg2iSM/QgtT5sRjaER17FkuSfkChM20O3IQncRe2V5df\n" +
                "G85GSJ8Tv5OTajXKQn6o7bC0n9Ke89SPp/WaA0jgb5CWHbw8jn7cI8Y//VtRiRKw\n" +
                "9Rnf0nUCgYEA9az92bcVm+hFd7Euf82KwHOMmoJZiwO1VJN4EgX5qGbBXsEVMH4t\n" +
                "GxO5AGggph+xsp6+5RMhjqim5JYt4SUWgbgbrpnKLL6aHtQxD48zdvul0sp3YMYS\n" +
                "32RjRkpGdsbXy9wrHnggQIF3NSkkXq93+feyNTvQPSUaD9/L4jCPCCMCgYEA5mPg\n" +
                "RLRktyclKZMvXaC82Puk4vQOTbMAmC/zQ/2VeoX0BQaxxH04KX7z4iLvY9FHuUG7\n" +
                "RI9WKk1vcz4OBFjFdx9Zc3oqV9AyZXg6+r0joJ8CO3kqcrL8go2vZ54nn26JjSdW\n" +
                "zb3JR2lCipcXZg6mBMt4Ug/ZLoZC4POKc8L9iJMCgYB0Cx5s9Bn05qXJf5ujKodb\n" +
                "mDjV0rRRAZNpO67/dGsUrFSyWSmVGkRcAdjk5EpnrZjV8j3hHkQ/ilIqrvVE36vd\n" +
                "4gTRWDszH2TVIw15d/6rOPp+srvoribD9jsePH9EI3BTDtAfOEajsYOJTMGtJ9zX\n" +
                "6bP3iSU3fIru7Jr1YFJvWQKBgEv6kiJ2T2SrbzEHzi4VbVItoxIJpJHxP3fxEEvS\n" +
                "3DH87R2fOI7xMM1Whb2FAeesQ/gPel/A2Yy33tJlm5/JWs3PM+PGXSJ6kbQu81xv\n" +
                "dclpxwWwDtSmvGmdqkQEv1Jv1MMNPy44saFwUDZ+X4QTvqKEK5j8iwYx1upyxq0o\n" +
                "mA4rAoGBAL1q7qFVO9T1wLnbSoNjBpaYHoX+h+FV+YJXEz2JK/snM/fT4vYRVDEt\n" +
                "TEq/hGxzlgo7+TIKxvEMtdz7vXqnwIGna79cjqoviYUBmKQYgXJDJBC3m5ADQjFg\n" +
                "8fuIydX8K0lO7ttfoNGFC8Te73kcSDNw9uJazOdTzEUSeHgMDsXl\n" +
                "-----END RSA PRIVATE KEY-----\n";
*/

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
/*
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
            RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
            RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(keySpecX509);
*/
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
