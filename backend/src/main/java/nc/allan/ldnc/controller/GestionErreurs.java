package nc.allan.ldnc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Transforme les erreurs en réponses JSON simples : { "message": "..." }.
 * Le site Angular affiche ce message à l'utilisateur.
 */
@RestControllerAdvice
public class GestionErreurs {

    // Erreurs lancées par les services (404 produit introuvable, 409 email déjà utilisé, 400 stock insuffisant...)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> erreurMetier(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()));
    }

    // Données invalides (annotations @NotBlank, @Email, @Size... avec @Valid) : on renvoie le premier message
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> donneesInvalides(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
