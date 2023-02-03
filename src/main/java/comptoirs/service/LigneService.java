package comptoirs.service;

import comptoirs.dao.CommandeRepository;
import comptoirs.dao.LigneRepository;
import comptoirs.dao.ProduitRepository;
import comptoirs.entity.Ligne;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated // Les contraintes de validatipn des méthodes sont vérifiées
public class LigneService {
    // La couche "Service" utilise la couche "Accès aux données" pour effectuer les traitements
    private final CommandeRepository commandeDao;
    private final LigneRepository ligneDao;
    private final ProduitRepository produitDao;

    // @Autowired
    // La couche "Service" utilise la couche "Accès aux données" pour effectuer les traitements
    public LigneService(CommandeRepository commandeDao, LigneRepository ligneDao, ProduitRepository produitDao) {
        this.commandeDao = commandeDao;
        this.ligneDao = ligneDao;
        this.produitDao = produitDao;
    }

    /**
     * <pre>
     * Service métier :
     *     Enregistre une nouvelle ligne de commande pour une commande connue par sa clé,
     *     Incrémente la quantité totale commandée (Produit.unitesCommandees) avec la quantite à commander
     * Règles métier :
     *     - le produit référencé doit exister
     *     - la commande doit exister
     *     - la commande ne doit pas être déjà envoyée (le champ 'envoyeele' doit être null)
     *     - la quantité doit être positive
     *     - On doit avoir une quantite en stock du produit suffisante
     * <pre>
     *
     *  @param commandeNum la clé de la commande
     *  @param produitRef la clé du produit
     *  @param quantite la quantité commandée (positive)
     *  @return la ligne de commande créée
     */
    @Transactional
    public Ligne ajouterLigne(Integer commandeNum, Integer produitRef, @Positive int quantite) {
        // On vérifie que le produit existe
        var produit = produitDao.findById(produitRef).orElseThrow();
        // On  vérifie que le produit n'est pas marqué indisponible
        if (Boolean.TRUE.equals(produit.getIndisponible())) {
            throw new IllegalArgumentException("Produit indisponible");
        }
        // On vérifie qu'il y a assez de stock
        if (produit.getUnitesEnStock() < quantite) {
            throw new IllegalArgumentException("Pas assez de stock");
        }
        // On vérifie que la commande existe
        var commande = commandeDao.findById(commandeNum).orElseThrow();
        // On vérifie que la commande n'est pas déjà envoyée
        if (commande.getEnvoyeele() != null) {
            throw new IllegalArgumentException("Commande déjà envoyée");
        }
        // On crée une ligne de commande pour cette commande
        var nouvelleLigne = new Ligne(commande, produit, quantite);
        // On enregistre la ligne de commande (génère la clé)
        ligneDao.save(nouvelleLigne);
        // On incrémente la quantité commandée
        produit.setUnitesCommandees(produit.getUnitesCommandees() + quantite);
        // Inutile de sauvegarder le produit, les entités modifiées par une transaction
        // sont automatiquement sauvegardées à la fin de la transaction
        return nouvelleLigne;
    }
}
