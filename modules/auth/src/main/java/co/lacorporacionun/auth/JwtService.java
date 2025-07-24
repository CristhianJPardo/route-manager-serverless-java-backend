package co.lacorporacionun.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JWTUtil {
    private static final String SECRET_KEY = System.getenv("JWT_SECRET");
    private static final long EXPIRATION_TIME_MS = 86400000; // 1 día

    public static String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }
}
