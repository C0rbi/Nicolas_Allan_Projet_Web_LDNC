package nc.allan.ldnc.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test unitaire du calcul du prix TTC. */
class ProduitTest {

    @Test
    void prixTtc_ajouteLaTvaAuPrixHt() {
        Produit produit = new Produit();
        produit.setPrixHt(100.0);
        produit.setTva(20.0);

        assertEquals(120.0, produit.getPrixTtc());
    }

    @Test
    void prixTtc_estArrondiAuCentime() {
        Produit produit = new Produit();
        produit.setPrixHt(9.99);   // 9,99 x 1,20 = 11,988
        produit.setTva(20.0);

        assertEquals(11.99, produit.getPrixTtc());
    }
}
