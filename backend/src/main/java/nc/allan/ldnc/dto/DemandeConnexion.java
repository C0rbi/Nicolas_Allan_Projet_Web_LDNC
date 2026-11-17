package nc.allan.ldnc.dto;

import jakarta.validation.constraints.NotBlank;

/** Données envoyées par le formulaire de connexion. */
public record DemandeConnexion(

        @NotBlank(message = "L'email est obligatoire")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        String motDePasse
) {
}
