package nc.allan.ldnc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nc.allan.ldnc.dto.DemandeConnexion;
import nc.allan.ldnc.dto.DemandeInscription;
import nc.allan.ldnc.dto.ReponseConnexion;
import nc.allan.ldnc.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/inscription  { "prenom": "...", "nom": "...", "email": "...", "motDePasse": "..." }
    @PostMapping("/inscription")
    @ResponseStatus(HttpStatus.CREATED)
    public ReponseConnexion inscription(@Valid @RequestBody DemandeInscription demande) {
        return authService.inscrire(demande);
    }

    // POST /api/auth/connexion  { "email": "...", "motDePasse": "..." }
    @PostMapping("/connexion")
    public ReponseConnexion connexion(@Valid @RequestBody DemandeConnexion demande) {
        return authService.connecter(demande);
    }
}
