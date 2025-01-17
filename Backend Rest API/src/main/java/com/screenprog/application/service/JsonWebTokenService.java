package com.screenprog.application.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class responsible for generating and validating JSON Web Tokens (JWT).
 * The JsonWebTokenService class provides methods for creating JWTs, retrieving claims, and verifying
 * the authenticity of the tokens. The tokens are signed using an HMAC SHA-256
 * algorithm with a secret key.
 *
 * <p>The secret key is generated upon the instantiation of this service and remains
 * constant for the duration of the application's runtime. The tokens generated
 * by this service are valid for a predetermined amount of time, which can be
 * configured as needed.</p>
 *
 * This service is typically used in conjunction with Spring Security to manage
 * authentication and authorization in a web application.
 *
 * @see <a href="https://www.baeldung.com/java-json-web-tokens-jjwt">Baeldung: JSON Web Tokens</a>
 * @see <a href="https://www.baeldung.com/java-hmac-sha-256">Baeldung: HmacSHA256</a>
 * @see KeyGenerator
 * @see SecretKey
 * @see Claims
 * @see Jwts
 */
@Service
public class JsonWebTokenService {

    private final String secretKey;

    /**
     * Here in this constructor a secret key is generated the reason behind generating.
     * it in constructor is to get a single secret key for every login.
     * HmacSHA256 is one of the algorithm.
     * Base64.getEncoder().encodeToString(byte[]) is used to covert byte[] (generated key) into string.
     * @see <a href="https://www.baeldung.com/java-hmac-sha-256">Baeldung: HmacSHA256</a>
     * @see KeyGenerator
     * @see SecretKey
     * */
    JsonWebTokenService(){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey generatedKey = keyGenerator.generateKey();
            secretKey = Base64.getEncoder().encodeToString(generatedKey.getEncoded());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a json web token for the given username. The token is valid for 30 minutes.
     * @param username the username to generate the token for.
     * @return {@link String} the json web token.
     * */
    public String generateToken(String username) {
        int EXPIRY_DURATION = 1000 * 60 * 30; // 30 minutes

        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRY_DURATION))
                .and()
                .signWith(getKey())
                .compact();
    }

//    Decoders.BASE64.decode(secretKey) is used to convert a string into byte array or bytes
    /**
     * This method retrieves the secret key used to sign the JWT.
     * The key is decoded from a Base64 string and used to create an HMAC SHA key.
     *
     * @return {@link SecretKey} The secret key used for signing the JWT.
     */
    private SecretKey getKey() {
        byte[] decodedKey = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }


//    This method is used to extract username from json web token(jwt)
//      by extractClaims() in which claimResolver excepts the method and evaluates it
    /**
     * Extracts the username from the provided JWT token.
     *
     * @param token the JSON Web Token from which the username is to be extracted.
     * @return {@link String} the username contained in the JWT token.
     */
    public String extractUsername(String token) {
        return extractClaims(token, (claims) -> claims.getSubject());
    }

    /**
     * This method extracts the asked claims.
     * R is the return type of this method and Function's as well.
     * @param token the JSON Web Token from which the claims are to be extracted.
     * @param claimsResolver function which accepts the claims and returns the requested claim.
     * @see JsonWebTokenService#extractAllClaims
     * @see Claims
     * */
    private <R> R extractClaims(String token, Function<Claims, R> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    /**
     * This method extracts all the claims from the provided JWT token.
     * The extracted claims contain the username and other information.
     *
     * @param token the JSON Web Token from which the claims are to be extracted
     * @return {@link Claims} object containing the extracted claims
     * @see Jwts
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * This method is used to validate the given JWT token against the
     * provided UserDetails.
     * <p>
     * It checks if the token is valid and the username contained in the
     * token matches the username of the provided UserDetails.
     * </p>
     * @param token the JSON Web Token to be validated
     * @param userDetails the UserDetails against which the token is to be validated
     * @return boolean indicating whether the token is valid or not
     * @see JsonWebTokenService#extractUsername
     * @see JsonWebTokenService#isTokenExpired
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks if the given JWT token is expired.
     * <p>
     * This method extracts the expiration time from the token and compares it
     * with the current date to determine if the token has expired.
     * </p>
     *
     * @param token the JSON Web Token to be checked for expiration
     * @return boolean indicating whether the token is expired (true) or not (false)
     * @see JsonWebTokenService#expirationTime
     */
    private boolean isTokenExpired(String token) {
        return expirationTime(token).before(new Date());
    }

    /**
     * Extracts the expiration time from the given JWT token.
     *
     * @param token the JSON Web Token from which the expiration time is to be extracted
     * @return {@link Date} the expiration time of the JWT token
     * @see JsonWebTokenService#extractClaims
     */
    private Date expirationTime(String token) {
        return extractClaims(token, (claim) -> claim.getExpiration());
    }
}

