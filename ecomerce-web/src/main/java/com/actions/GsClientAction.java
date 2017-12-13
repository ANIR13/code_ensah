package com.actions;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultPath;
import org.springframework.beans.factory.annotation.Autowired;

import com.bo.Client;
import com.services.GsClientService;

@ResultPath("/pages/")
public class GsClientAction extends BaseAction {

	/**
	 * utilis�e pour stocker la liste des produits destin�s � afficher dans la
	 * vue
	 */
	private List<Client> listClients;

	/** utilis� pour stocker les donn�es du produit saisies dans la vue */
	private Client client;

	/** injection du service m�tier */
	@Autowired
	private GsClientService gsClientService;

	@Action(value = "addClient", results = { @Result(name = "success", location = "addClient.jsp") })
	public String addClient() {

		// On utilise le service m�tier pour sauvegarder en base de donn�es le
		// produit
		// rempli par les donn�es copi�es automatiquement par struts de la vue
		// vers
		// l'objet produit
		gsClientService.addClient(client);

		// On retoune la page associ�e � success
		return SUCCESS;

	}

	@Action(value = "getAllClients", results = { @Result(name = "success", location = "listClients.jsp") })
	public String getAllClients() {

		// En utiliant le service m�tier on charge la liste des produit et on la
		// stocke
		// dans l'attribut qui sera lu depuis une vue
		listClients = gsClientService.getAllClients();

		// on redirige vers la vue associ� � la cl� sucess
		return SUCCESS;

	}

	public List<Client> getListClients() {
		return listClients;
	}

	public void setListClients(List<Client> listClients) {
		this.listClients = listClients;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public GsClientService getGsClientService() {
		return gsClientService;
	}

	public void setGsClientService(GsClientService gsClientService) {
		this.gsClientService = gsClientService;
	}

}
