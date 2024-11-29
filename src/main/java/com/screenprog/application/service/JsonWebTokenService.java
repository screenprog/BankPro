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

/*This class is used to generate a json web token */
@Service
public class JsonWebTokenService {

    private final String secretKey;

    /*Here in this constructor a secret key is generated the reason behind generating
     * it in constructor is to get a single secret key for every login*/
    /**************Inside the method***************/
    /*HmacSHA256 is one of the algorithm*/
    /*Base64.getEncoder().encodeToString(byte[]) is used to covert byte[] (generated key)
     into string*/
    JsonWebTokenService(){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey generatedKey = keyGenerator.generateKey();
            secretKey = Base64.getEncoder().encodeToString(generatedKey.getEncoded());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

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

    /*Decoders.BASE64.decode(secretKey) is used to convert a string into byte array or bytes*/
    private SecretKey getKey() {
        byte[] decodedKey = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }


    /*This method is used to extract username from json web token(jwt)
     * by extractClaims() in which claimResolver excepts the method and evaluates it*/
    public String extractUsername(String token) {
        return extractClaims(token, (claims) -> claims.getSubject());
    }

    /*This method extracts the asked claims */
    /*R is the return type of this method and Function's as well*/
    private <R> R extractClaims(String token, Function<Claims, R> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return expirationTime(token).before(new Date());
    }

    private Date expirationTime(String token) {
        return extractClaims(token, (claim) -> claim.getExpiration());
    }
}

