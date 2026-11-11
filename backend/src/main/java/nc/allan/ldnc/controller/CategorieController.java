package nc.allan.ldnc.controller;

import lombok.RequiredArgsConstructor;
import nc.allan.ldnc.entity.Categorie;
import nc.allan.ldnc.repository.CategorieRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Liste des catégories (menu du site, filtres, formulaire d'administration). */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    // Pas de règle métier : le contrôleur utilise directement le repository
    private final CategorieRepository categorieRepository;

    @GetMapping
    public List<Categorie> lister() {
        return categorieRepository.findAll(Sort.by("id"));
    }
}
