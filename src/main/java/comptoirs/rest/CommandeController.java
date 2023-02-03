package comptoirs.rest;

import comptoirs.dto.CommandeDTO;
import comptoirs.dto.LigneDTO;
import comptoirs.entity.Commande;
import comptoirs.service.LigneService;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import comptoirs.service.CommandeService;

@RestController // Cette classe est un contrôleur REST
@RequestMapping(path = "/comptoirs/commande") // chemin d'accès
public class CommandeController {
	private final CommandeService commandeService;
	private final LigneService ligneService;
	private final ModelMapper mapper;
	// @Autowired
	public CommandeController(CommandeService commandeService, LigneService ligneService, ModelMapper mapper) {
		this.commandeService = commandeService;
		this.ligneService = ligneService;
		this.mapper = mapper;
	}

	@PostMapping("ajouterPour/{clientCode}")
	public CommandeDTO ajouter(@PathVariable String clientCode) {
		Commande commande = commandeService.creerCommande(clientCode);
		return mapper.map(commande, CommandeDTO.class);		 
	}	
	@PostMapping("expedier/{commandeNum}")
	public CommandeDTO expedier(@PathVariable Integer commandeNum) {
		return mapper.map(commandeService.enregistreExpedition(commandeNum), CommandeDTO.class);
	}
	@PostMapping("ajouterLigne")
	public LigneDTO ajouterLigne(@RequestParam Integer commandeNum, @RequestParam Integer produitRef, @RequestParam Integer quantite) {
		var ligne = ligneService.ajouterLigne(commandeNum, produitRef, quantite);
		return mapper.map(ligne, LigneDTO.class);
	}
}
