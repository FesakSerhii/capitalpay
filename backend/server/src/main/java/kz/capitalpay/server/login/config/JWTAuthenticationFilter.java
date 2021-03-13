package kz.capitalpay.server.login.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import kz.capitalpay.server.login.model.ApplicationUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

import static kz.capitalpay.server.login.config.SecurityConstants.*;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

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
        String token = JWT.create()
                .withSubject(((User) authResult.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
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

//
//        String publicKeyContent = "-----BEGIN PUBLIC KEY-----\n" +
//                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3RlFMRLfb0n7pve08qgX\n" +
//                "Xv5ajtREyGknir8Cbwwsq/FWwWjQDFo3OZNO3zpPPfCpk0L/VUwkWWro5Y6wFBzC\n" +
//                "bfHKfir2Ibuqy3oM87v2p/jdMmPKxx6UtHQ3vrrSAJgZRw7bhMtURk4xy0y/N3kN\n" +
//                "uARLupQootizCRef01cNZTWz0mY28MrJrpGVpNxr/APcan5bJum9VI1z9dpBL+W+\n" +
//                "JuNL7NasUS79aneP3L4V/3nckntxhON52adaSAyVLuuUmd/NbIgYLvSkuI7pZ+/U\n" +
//                "7/k8XkAnTjxbnRsezssRMgJYuCNthQ96h6vdxViurQQhMz2y/jWpncYtBSPsbwtE\n" +
//                "GQIDAQAB\n" +
//                "-----END PUBLIC KEY-----";


//
//        String privateKeyContent = "-----BEGIN PRIVATE KEY-----\n" +
//                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDdGUUxEt9vSfum\n" +
//                "97TyqBde/lqO1ETIaSeKvwJvDCyr8VbBaNAMWjc5k07fOk898KmTQv9VTCRZaujl\n" +
//                "jrAUHMJt8cp+KvYhu6rLegzzu/an+N0yY8rHHpS0dDe+utIAmBlHDtuEy1RGTjHL\n" +
//                "TL83eQ24BEu6lCii2LMJF5/TVw1lNbPSZjbwysmukZWk3Gv8A9xqflsm6b1UjXP1\n" +
//                "2kEv5b4m40vs1qxRLv1qd4/cvhX/edySe3GE43nZp1pIDJUu65SZ381siBgu9KS4\n" +
//                "juln79Tv+TxeQCdOPFudGx7OyxEyAli4I22FD3qHq93FWK6tBCEzPbL+Namdxi0F\n" +
//                "I+xvC0QZAgMBAAECggEAM0vansEXL7XFef58b9QJ4BM5JWwgLTMgZnNceDEl6BqN\n" +
//                "G0BbDJKdv3Dq2qWnnuJIJY9kuAB7aneAY0oQlgn6HhH7/fuvsTHeNWkyuhXA60W0\n" +
//                "5Cz87eetbpSMHmcvs82WB8NtsMiP2+Pmb5l1v8QJJ8lvXHFlYCrQlupxPVI0HGfD\n" +
//                "xfXyYnPBobki94RFRKMKE03JHVt1lB1eE5hiXZ4WU5A1DOwxh+jORtqSDaJIz9CC\n" +
//                "1PmxGNoRHXsWS5J+QKEzbQ7chCdxF7ZXl18bzkZInxO/k5NqNcpCfqjtsLSf0p7z\n" +
//                "1I+n9ZoDSOBvkJYdvDyOftwjxj/9W1GJErD1Gd/SdQKBgQD1rP3ZtxWb6EV3sS5/\n" +
//                "zYrAc4yaglmLA7VUk3gSBfmoZsFewRUwfi0bE7kAaCCmH7Gynr7lEyGOqKbkli3h\n" +
//                "JRaBuBuumcosvpoe1DEPjzN2+6XSyndgxhLfZGNGSkZ2xtfL3CseeCBAgXc1KSRe\n" +
//                "r3f597I1O9A9JRoP38viMI8IIwKBgQDmY+BEtGS3JyUpky9doLzY+6Ti9A5NswCY\n" +
//                "L/ND/ZV6hfQFBrHEfTgpfvPiIu9j0Ue5QbtEj1YqTW9zPg4EWMV3H1lzeipX0DJl\n" +
//                "eDr6vSOgnwI7eSpysvyCja9nniefbomNJ1bNvclHaUKKlxdmDqYEy3hSD9kuhkLg\n" +
//                "84pzwv2IkwKBgHQLHmz0GfTmpcl/m6Mqh1uYONXStFEBk2k7rv90axSsVLJZKZUa\n" +
//                "RFwB2OTkSmetmNXyPeEeRD+KUiqu9UTfq93iBNFYOzMfZNUjDXl3/qs4+n6yu+iu\n" +
//                "JsP2Ox48f0QjcFMO0B84RqOxg4lMwa0n3Nfps/eJJTd8iu7smvVgUm9ZAoGAS/qS\n" +
//                "InZPZKtvMQfOLhVtUi2jEgmkkfE/d/EQS9LcMfztHZ84jvEwzVaFvYUB56xD+A96\n" +
//                "X8DZjLfe0mWbn8lazc8z48ZdInqRtC7zXG91yWnHBbAO1Ka8aZ2qRAS/Um/Uww0/\n" +
//                "LjixoXBQNn5fhBO+ooQrmPyLBjHW6nLGrSiYDisCgYEAvWruoVU71PXAudtKg2MG\n" +
//                "lpgehf6H4VX5glcTPYkr+ycz99Pi9hFUMS1MSr+EbHOWCjv5MgrG8Qy13Pu9eqfA\n" +
//                "gadrv1yOqi+JhQGYpBiBckMkELebkANCMWDx+4jJ1fwrSU7u21+g0YULxN7veRxI\n" +
//                "M3D24lrM51PMRRJ4eAwOxeU=\n" +
//                "-----END PRIVATE KEY-----";


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
