package com.services;

import java.util.List;

import com.bo.Categorie;

/**
 * interface du service metier permettant d'impl�menter la logique m�tier et
 * applicative li�es � la gestion des produits
 * 
 * @author T.BOUDAA
 *
 */
public interface GsCategorieService {

	/** implemente le service m�tier permettant de sauvgarder un produit */
	void addCategorie(Categorie p);

	/** impl�mente le service m�tier permettant de r�cup�rer tous les produits */
	List<Categorie> getAllCategories();

}