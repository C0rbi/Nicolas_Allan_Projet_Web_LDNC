package nc.allan.ldnc.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Règles de sécurité de l'API : qui a le droit d'appeler quelle adresse.
 * Le contrôle est fait ici, côté serveur : masquer un bouton dans Angular ne suffit pas.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtresDeSecurite(HttpSecurity http, JwtService jwtService) throws Exception {
        http
                // Pas de cookie de session (le token JWT est envoyé dans un en-tête) : pas de risque CSRF
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(regles -> regles
                        // Page d'erreur interne de Spring : doit rester accessible,
                        // sinon une erreur 403 ou 400 serait transformée en 401
                        .requestMatchers("/error").permitAll()
                        // Ouvert à tous les visiteurs
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/produits/**", "/api/categories", "/api/marques").permitAll()
                        // Ajouter, modifier, supprimer un produit : administrateurs seulement
                        .requestMatchers("/api/produits/**").hasRole("ADMIN")
                        // Tout le reste (le panier) : il faut être connecté
                        .anyRequest().authenticated())

                // Visiteur sans token valide sur une route protégée : réponse 401 (non connecté)
                // Connecté mais sans le bon rôle : réponse 403 (interdit)
                .exceptionHandling(erreurs -> erreurs.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                // Notre filtre lit le token avant le traitement de la requête
                .addFilterBefore(new JwtFiltre(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Hachage des mots de passe avec BCrypt (empreinte salée, impossible à inverser). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** CORS : autorise le site Angular (autre port, donc autre origine) à appeler l'API. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${ldnc.front.url}") String urlFront) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(urlFront));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
