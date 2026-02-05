package AlessiaMonasteri.BagECommerce.security;

import AlessiaMonasteri.BagECommerce.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JWTTools {
    private static String secret;

    public JWTTools(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public String createToken(User user){
        return Jwts.builder()
                .setIssuedAt(new Date (System.currentTimeMillis()))
                .setExpiration(new Date (System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // validità token di 1 giorno
                .setSubject(String.valueOf(user.getId()))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    // Verifica che un token sia valido (firma + scadenza + struttura)
    public void verifyToken(String token) {
        Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).build().parseClaimsJws(token);
    }

    // Estrae l'UUID utente dal subject del token
    public UUID getIDFromToken(String accessToken) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();

        return UUID.fromString(subject);
    }

}
