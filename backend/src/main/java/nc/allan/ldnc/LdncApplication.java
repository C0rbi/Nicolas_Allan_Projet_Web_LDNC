package nc.allan.ldnc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

/**
 * Point d'entrée de l'API LDNC.
 * Lancement : .\mvnw.cmd spring-boot:run (dans le dossier backend) puis http://localhost:8080/api/produits
 *
 * UserDetailsServiceAutoConfiguration est désactivé : les comptes sont en base
 * et vérifiés par AuthService, Spring n'a pas besoin de créer un utilisateur par défaut.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class LdncApplication {

	public static void main(String[] args) {
		SpringApplication.run(LdncApplication.class, args);
	}

}
