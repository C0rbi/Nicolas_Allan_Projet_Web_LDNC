package nc.allan.ldnc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** Vérifie que l'application démarre (configuration, beans, accès à la base de test). */
@SpringBootTest
@ActiveProfiles("test")
class LdncApplicationTests {

	@Test
	void contextLoads() {
	}

}
