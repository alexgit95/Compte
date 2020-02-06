package compte.services;

import java.util.List;

import compte.model.Categorie;
import compte.model.Depense;

public interface DaoServices {

	/**
	 * Permet d'inserer une nouvelle depense
	 * @param toCreate
	 */
	void insert(Depense toCreate);
	
	
	/**
	 * Permet de recuperer depenses de la categorie et du mois
	 * @return
	 */
	List<Depense> getDepenseByMonth(int month, int year, String categorie);
	
	/**
	 * Permet de recuperer toutes les infos des categories
	 * @param year
	 * @return
	 */
	List<Categorie> getCategories();


	
}
