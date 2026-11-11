package nc.allan.ldnc.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filtre exécuté avant chaque requête.
 * Si la requête contient l'en-tête "Authorization: Bearer <token>" avec un token valide,
 * l'utilisateur est considéré comme connecté (avec ses rôles) pendant cette requête.
 * Sinon la requête continue en tant que visiteur : SecurityConfig décide si elle est autorisée.
 */
public class JwtFiltre extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFiltre(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest requete, HttpServletResponse reponse, FilterChain chaine)
            throws ServletException, IOException {

        String entete = requete.getHeader("Authorization");

        if (entete != null && entete.startsWith("Bearer ")) {
            String token = entete.substring(7);   // on enlève "Bearer "
            try {
                Claims contenu = jwtService.lireToken(token);

                // Les rôles du token deviennent des "autorités" Spring : ADMIN -> ROLE_ADMIN
                List<SimpleGrantedAuthority> autorites = new ArrayList<>();
                for (Object role : contenu.get("roles", List.class)) {
                    autorites.add(new SimpleGrantedAuthority("ROLE_" + role));
                }

                var authentification = UsernamePasswordAuthenticationToken.authenticated(
                        contenu.getSubject(), null, autorites);
                SecurityContextHolder.getContext().setAuthentication(authentification);
            } catch (JwtException | IllegalArgumentException e) {
                // Token expiré ou modifié : la requête continue comme celle d'un visiteur
                SecurityContextHolder.clearContext();
            }
        }

        chaine.doFilter(requete, reponse);
    }
}
