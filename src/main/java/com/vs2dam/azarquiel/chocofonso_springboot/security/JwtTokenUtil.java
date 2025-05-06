package com.vs2dam.azarquiel.chocofonso_springboot.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.vs2dam.azarquiel.chocofonso_springboot.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKey; // Este valor deberías configurarlo como una variable de entorno para mayor seguridad

    private static final long EXPIRATION_TIME = 86400000; // 1 día en milisegundos

    // Genera el token JWT
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, secretKey) // Firma el JWT con el algoritmo HMAC-SHA512
                .compact();
    }

    // Valida el token JWT
    public boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Obtiene el nombre de usuario (subject) del token
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // Obtiene la fecha de expiración del token
    private Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    // Extrae las claims del token
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // Usa la clave secreta para parsear el token
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Verifica si el token ha expirado
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
