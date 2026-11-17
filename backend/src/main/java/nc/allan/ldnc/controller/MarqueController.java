package nc.allan.ldnc.controller;

import lombok.RequiredArgsConstructor;
import nc.allan.ldnc.entity.Marque;
import nc.allan.ldnc.repository.MarqueRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Liste des marques (filtres, formulaire d'administration). */
@RestController
@RequestMapping("/api/marques")
@RequiredArgsConstructor
public class MarqueController {

    // Pas de règle métier : le contrôleur utilise directement le repository
    private final MarqueRepository marqueRepository;

    @GetMapping
    public List<Marque> lister() {
        return marqueRepository.findAll(Sort.by("nom"));
    }
}
