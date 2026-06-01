package nc.allan.ldnc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nc.allan.ldnc.entity.Produit;
import nc.allan.ldnc.repository.ProduitRepository;

@RestController
@RequestMapping("/api/produit")
@CrossOrigin(origins = "http://localhost:4200") // pour autoriser Angular à accéder à cette API
public class ProduitController {
    
    @Autowired
    private ProduitRepository repo;

    @GetMapping
    public List<Produit> getProduit() {
        return repo.findAll();
    }
}