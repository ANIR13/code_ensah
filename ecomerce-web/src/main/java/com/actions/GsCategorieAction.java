package com.actions;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultPath;
import org.springframework.beans.factory.annotation.Autowired;

import com.bo.Categorie;
import com.bo.Client;
import com.services.GsCategorieService;
import com.services.GsClientService;

@ResultPath("/pages/")
public class GsCategorieAction extends BaseAction {

	/**
	 * utilis�e pour stocker la liste des produits destin�s � afficher dans la
	 * vue
	 */
	private List<Categorie> listCategories;

	/** utilis� pour stocker les donn�es du produit saisies dans la vue */
	private Categorie categorie;

	/** injection du service m�tier */
	@Autowired
	private GsCategorieService gsCategorieService;

	@Action(value = "addCategorie", results = { @Result(name = "success", location = "addCategorie.jsp") })
	public String addClient() {

		// On utilise le service m�tier pour sauvegarder en base de donn�es le
		// produit
		// rempli par les donn�es copi�es automatiquement par struts de la vue
		// vers
		// l'objet produit
		gsCategorieService.addCategorie(categorie);

		// On retoune la page associ�e � success
		return SUCCESS;

	}

	@Action(value = "getAllCategories", results = { @Result(name = "success", location = "listCategories.jsp") })
	public String getAllCategories() {

		// En utiliant le service m�tier on charge la liste des produit et on la
		// stocke
		// dans l'attribut qui sera lu depuis une vue
		listCategories = gsCategorieService.getAllCategories();

		// on redirige vers la vue associ� � la cl� sucess
		return SUCCESS;

	}

	public List<Categorie> getListCategories() {
		return listCategories;
	}

	public void setListCategories(List<Categorie> listCategories) {
		this.listCategories = listCategories;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public GsCategorieService getGsCategorieService() {
		return gsCategorieService;
	}

	public void setGsCategorieService(GsCategorieService gsCategorieService) {
		this.gsCategorieService = gsCategorieService;
	}



}
