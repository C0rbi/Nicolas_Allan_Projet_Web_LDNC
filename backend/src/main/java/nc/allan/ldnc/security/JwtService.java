package nc.allan.ldnc.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Crée et vérifie les tokens JWT.
 * Un token contient l'email de l'utilisateur, ses rôles et une date d'expiration.
 * Il est signé avec une clé secrète connue du serveur seulement :
 * s'il est modifié dans le navigateur, la signature ne correspond plus et il est refusé.
 */
@Service
public class JwtService {

    private final SecretKey cle;
    private final long dureeMinutes;

    public JwtService(@Value("${ldnc.jwt.secret}") String secret,
                      @Value("${ldnc.jwt.duree-minutes}") long dureeMinutes) {
        this.cle = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.dureeMinutes = dureeMinutes;
    }

    public String creerToken(String email, List<String> roles) {
        Instant maintenant = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(Date.from(maintenant))
                .expiration(Date.from(maintenant.plus(dureeMinutes, ChronoUnit.MINUTES)))
                .signWith(cle, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Vérifie la signature et la date d'expiration du token, puis renvoie son contenu.
     * Lance une JwtException si le token est invalide ou expiré.
     */
    public Claims lireToken(String token) {
        return Jwts.parser()
                .verifyWith(cle)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
