package com.muema.oauth21.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenService {

    private static final String SECRET_KEY = "48A5360D50EFAE9A612284859CDD1D8F773EDFBEDF89D5B7C83F294392946455";
    public static final long EXPIRATION_TIME = 3600000; // 1 hour

    public String generateToken(String clientId, String username, String firstName, String lastName, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(clientId)
                .claim("username", username)
                .claim("firstname", firstName)
                .claim("lastname", lastName)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            // Parse the token
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(SECRET_KEY) // Use the same secret key used for signing
                    .parseClaimsJws(token);

            // If token is valid, you can perform additional claims checks here
            Claims claims = claimsJws.getBody();
            // For example, check if the token has expired
            Date expiration = claims.getExpiration();
            return !expiration.before(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            // Log the exception or handle it accordingly
            return false; // Token is invalid
        }
    }

}