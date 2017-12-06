package com.genericdao;

import java.util.ArrayList;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * A simple Generic DAO used to execute CRUD opertaion
 * 
 * @author boudaa
 * 
 * @param <T>
 *            type of java
 */
public class GenericDAOImpl<T> {

	/** La classe BO manipul� par le DAO */
	protected Class<T> boClass;

	/** Utilis� par tous les DAOs pour enregitrer les logs */
	protected final Logger LOGGER;

	/** la fabrique des session */
	protected SessionFactory sf = HibernateUtil.getSessionFactory();

	public GenericDAOImpl(Class<T> pClass) {

		boClass = pClass;

		LOGGER = Logger.getLogger(boClass);

		LOGGER.debug("le dao de " + boClass + " a �t� initialis�");
	}

	/**
	 * Annule la transaction
	 * 
	 * @param tx
	 * @param ex
	 * @throws Exception
	 */
	protected void handleDaoOpError(Transaction tx, RuntimeException ex) {

		// Si l'objet de tx est initialis�
		if (tx != null) {

			// Annuler la transaction
			tx.rollback();
		}

		// On trace l'erreur dans le journal
		LOGGER.error("erreur d'annulation de la transaction � cause de l'exception : " + ex);

		// On remonte l'exception (faut pas cacher les probl�mes)
		throw new DataAccessLayerException(ex);

	}

	/**
	 * Permet de fermer une session Hibernate
	 * 
	 * @param s
	 */
	protected void closeSession(Session s) {

		// Si la session existe et toujours ouverte
		if (s != null && s.isOpen()) {
			s.close();
		}
	}

	/**
	 * Permet d'obtenir une session Hibernate
	 * 
	 * @return
	 */
	protected Session getSession() {
		return sf.getCurrentSession();
	}

	/**
	 * Permet de savoir si la session a d�j� une transaction active (d�j�
	 * initialis�e par une autre m�thode)
	 * 
	 * @param s
	 * @return
	 */
	protected boolean anActiveTransactionExist(Session s) {
		if (s != null && s.getTransaction() != null)
			// retourne true si la session a d�j� une transaction active
			return s.getTransaction().isActive();

		return false;
	}

	/**
	 * Permet de persister (sauvgarder) un objet dans la base de donn�es
	 * 
	 * @param o
	 * 
	 * @return
	 */
	public Long create(T o) {

		LOGGER.debug("appel de la m�thode create");

		// On obtient la session en cours
		Session s = getSession();

		Long id = null;

		// Si une transaction est d�j� d�marr� (typiquement par un filtre ou la
		// couche service m�tier)
		if (anActiveTransactionExist(s)) {

			LOGGER.debug("le DAO utilise la transaction  d�j� d�marr�e dans les couches sup�rieures");

			// Dans ce cas on app�le directement la m�thode hibernate
			id = (Long) s.save(o);

		} else {
			// Dans le cas contraire le DAO va d�marrer sa propre transaction

			LOGGER.debug("le DAO initialise sa propre transaction");

			Transaction tx = null;

			try {

				// On d�marre une transaction localement
				tx = s.beginTransaction();

				// On fait les op�ration

				id = (Long) s.save(o);

				// On valide la transaction
				tx.commit();
			} catch (RuntimeException ex) {
				// En cas de probl�mes on annule la transaction
				// et on trace
				handleDaoOpError(tx, ex);
			} finally {
				// Fermer la session s'elle est encore ouverte
				closeSession(s);
			}
		}

		LOGGER.debug("fin d'appel de la m�thode save avec succ�s ");

		return id;
	}

	/**
	 * Utilis� pour mettre � jour une entit� d�tach�e (ratache un objet � la
	 * session pour qu'il soit g�r� => donc synchronis� automatiquement avec la
	 * base de donn�es)
	 * 
	 * @param o
	 */
	public void update(T o) {

		LOGGER.debug("appel de la m�thode save");

		// On obtient la session en cours
		Session s = getSession();

		// Si une transaction est d�j� d�marr� (typiquement par un filtre ou la
		if (anActiveTransactionExist(s)) {

			// Dans ce cas c'est la couche sup�rieure qui gere la session et la
			// transaction
			s.update(o);

		} else {
			LOGGER.debug("le DAO initialise sa propre transaction");

			Transaction tx = null;

			try {

				// On d�marre une transaction localement
				tx = s.beginTransaction();

				s.update(o);

				// On valide la transaction
				tx.commit();
			} catch (RuntimeException ex) {
				// En cas de probl�mes en annule la transaction
				handleDaoOpError(tx, ex);
			} finally {
				// Fermer la session s'elle est encore ouverte
				closeSession(s);
			}
		}

		LOGGER.debug("fin d'appel de la m�thode save avec succ�s ");

	}

	/**
	 * permet de r�cup�rer toute les entit�s de type T
	 * 
	 * @return
	 * @throws EntityNotFoundException
	 */
	public List<T> getAll() throws EntityNotFoundException {

		LOGGER.debug("appel de la m�thode save");

		// On obtient la session en cours
		Session s = getSession();

		List<T> list = null;

		// Si une transaction est d�j� d�marr� (typiquement par un filtre ou la
		if (anActiveTransactionExist(s)) {

			// Dans ce cas c'est la couche sup�rieure qui gere la session et la
			// transaction

			// On a utilis� l'API Criteria de Hibernate
			list = s.createCriteria(boClass).list();

		} else {
			LOGGER.debug("le DAO initialise sa propre transaction");

			Transaction tx = null;

			try {

				// On d�marre une transaction localement
				tx = s.beginTransaction();

				list = s.createCriteria(boClass).list();

				tx.commit();
			} catch (RuntimeException ex) {
				// En cas de probl�mes en annule la transaction
				handleDaoOpError(tx, ex);
			} finally {
				// Fermer la session s'elle est encore ouverte
				closeSession(s);
			}
		}

		// Si la liste est vide on d�clenche un exception (c'est un choix de
		// conception, on peut �galement retourner une liste vide)
		if (list == null || list.size() == 0)
			throw new EntityNotFoundException();

		return list;
	}

	/**
	 * permet de r�cup�rer toute les entit�s de type T mais avec une limite de
	 * nombre de r�sultats � retourner
	 * 
	 * @return
	 * @throws EntityNotFoundException
	 */
	public List<T> getAll(int maxResults) throws EntityNotFoundException {

		LOGGER.debug("appel de la m�thode save");

		// On obtient la session en cours
		Session s = getSession();

		List<T> list = null;

		// Si une transaction est d�j� d�marr� (typiquement par un filtre ou la
		if (anActiveTransactionExist(s)) {

			// Dans ce cas c'est la couche sup�rieure qui gere la session et la
			// transaction
			list = s.createCriteria(boClass).setMaxResults(maxResults).list();

		} else {
			LOGGER.debug("le DAO initialise sa propre transaction");

			Transaction tx = null;

			try {

				// On d�marre une transaction localement
				tx = s.beginTransaction();

				list = s.createCriteria(boClass).setMaxResults(maxResults).list();

				tx.commit();
			} catch (RuntimeException ex) {
				// En cas de probl�mes en annule la transaction
				handleDaoOpError(tx, ex);
			} finally {
				// Fermer la session s'elle est encore ouverte
				closeSession(s);
			}
		}

		if (list == null || list.size() == 0)
			throw new EntityNotFoundException();

		return list;

	}

	/**
	 * Permet de faire une recherche avec une condition sur une colonne
	 * 
	 * @param pColName
	 *            nom de la colonne
	 * @param pColVal
	 *            la valeur de la colonne
	 * @param pClassName
	 *            le nom de la classe (et non pas de la table)
	 * @return
	 * @throws EntityNotFoundException
	 */
	public List<T> getByColValue(String pColName, String pColVal, String pClassName) throws EntityNotFoundException {

		LOGGER.debug("appel de la m�thode getByColValue");

		// On obtient la session en cours
		Session s = getSession();

		List<T> list = new ArrayList<T>();

		// Si une transaction est d�j� d�marr� (typiquement par un filtre ou la
		if (anActiveTransactionExist(s)) {

			// Dans ce cas c'est la couche sup�rieure qui gere la session et la
			// transaction

			// Une requete param�tr�e �crite avec HQL de Hibernate
			Query q = s.createQuery("from " + pClassName + " where " + pColName + "=?");
			q.setParameter(0, pColVal);
			list = q.list();

		} else {
			LOGGER.debug("le DAO initialise sa propre transaction");

			Transaction tx = null;

			try {

				// On d�marre une transaction localement
				tx = s.beginTransaction();

				// Une requete param�tr�e �crite avec HQL de Hibernate
				Query q = s.createQuery("from " + pClassName + " where " + pColName + "=?");
				q.setParameter(0, pColVal);
				list = q.list();

				tx.commit();
			} catch (RuntimeException ex) {
				// En cas de probl�mes en annule la transaction
				handleDaoOpError(tx, ex);
			} finally {
				// Fermer la session s'elle est encore ouverte
				closeSession(s);
			}
		}

		return list;
	}

	/**
	 * Permet de supprimer une entit�e d�finit par son identifiant
	 * 
	 * @param pId
	 * @throws EntityNotFoundException
	 * @throws DataAccessLayerException
	 */
	public void delete(Long pId) throws EntityNotFoundException, DataAccessLayerException {

		LOGGER.debug("appel de la m�thode delete");

		T obj = (T) findById(pId);

		// On obtient la session en cours
		Session s = getSession();

		Long id = null;

		// Si une transaction est d�j� d�marr� (typiquement par un filtre ou la
		if (anActiveTransactionExist(s)) {

			// Dans ce cas c'est la couche sup�rieure qui gere la session et la
			// transaction
			s.delete(obj);

		} else {
			LOGGER.debug("le DAO initialise sa propre transaction");

			Transaction tx = null;

			try {

				// On d�marre une transaction localement
				tx = s.beginTransaction();

				s.delete(obj);

				// On valide la transaction
				tx.commit();
			} catch (RuntimeException ex) {
				// En cas de probl�mes en annule la transaction
				handleDaoOpError(tx, ex);
			} finally {
				// Fermer la session s'elle est encore ouverte
				closeSession(s);
			}
		}

		LOGGER.debug("fin d'appel de la m�thode delete avec succ�s ");

	}

	/**
	 * Permet de rechercher une entit� par son identifiant
	 * 
	 * @param pId
	 * @return
	 * @throws EntityNotFoundException
	 * @throws DataAccessLayerException
	 */
	public T findById(Long pId) throws EntityNotFoundException, DataAccessLayerException {
		LOGGER.debug("appel de la m�thode findById");

		// On obtient la session en cours
		Session s = getSession();

		T obj = null;

		// Si une transaction est d�j� d�marr� (typiquement par un filtre ou la
		if (anActiveTransactionExist(s)) {

			// Dans ce cas c'est la couche sup�rieure qui gere la session et la
			// transaction
			obj = (T) s.get(boClass, pId);

		} else {
			LOGGER.debug("le DAO initialise sa propre transaction");

			Transaction tx = null;

			try {

				// On d�marre une transaction localement
				tx = s.beginTransaction();

				obj = (T) s.get(boClass, pId);

				tx.commit();
			} catch (RuntimeException ex) {
				// En cas de probl�mes en annule la transaction
				handleDaoOpError(tx, ex);
			} finally {
				// Fermer la session s'elle est encore ouverte
				closeSession(s);
			}
		}

		if (obj == null)
			throw new EntityNotFoundException();

		return obj;
	}

	/**
	 * Permet de tester si une entit� existe en base de donn�es
	 * 
	 * @param pId
	 * @return
	 * @throws DataAccessLayerException
	 */
	public boolean exists(Long pId) throws DataAccessLayerException {

		try {
			findById(pId);

		} catch (EntityNotFoundException ex) {
			return false;
		}

		return true;

	}

}
