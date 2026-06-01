export class Produit {
    constructor(
        public id: number,
        public nom: string,
        public description: string,
        public prix_ht: number,
        public tva: number,
        public stock: number,
        public marque_id: number,
        public categorie_id: number,
        public date_ajout: string
    ) { }
}