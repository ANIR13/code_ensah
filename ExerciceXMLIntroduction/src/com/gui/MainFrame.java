package com.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.bo.Animal;

/**
 * Fentre principale de l'application
 * 
 * @author Tarik BOUDAA
 *
 */
public class MainFrame extends JFrame {

	/** Paneau conteneur */
	private JanglePanel janglePan;

	/** Permet de lancer les d�placements du lion */
	private JButton btnLion = new JButton("D�placer Lion");

	/** Permet de lancer les d�placements du Lapin */
	private JButton btnLapin = new JButton("D�placer Lapin");

	private JLabel stateLabel = new JLabel("...........");

	public MainFrame() {

		// Cr�er le conteneur
		janglePan = new JanglePanel();

		// Donner un titre � la fentre
		setTitle("Application XML Introduction");
		// Le size de la fenetre, normalement faut le calculer dynamiquement en
		// fonction de l'�cran
		setSize(600, 500);

		// La fermeture de la fentre doit arreter le processus de l'application
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// traitelent de l'�venemt associ� au bouton d�placer lion

		btnLion.addActionListener(new ActionListener() {

			// S'execute lorsqu'on clique sur le bouton
			public void actionPerformed(ActionEvent evn) {

				// On affiche dans l'interface l'�tat
				stateLabel.setText("Le lion est en mouvement");

				// On affiche dans la console le thread qui execute le code
				// suivant
				displayUtils("D�placement d'un lion...");

				// L'animal qui se trouve dans la position 0 (le lion dans notre
				// cas)
				janglePan.choisirAnimal(0);

				// executer dans un nouveau thread le traitement long qu'est le
				// d�placement d'un animal
				doLongTask();
			}

		});

		btnLapin.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evn) {

				// On affiche dans l'interface l'�tat
				stateLabel.setText("Le lapin est en mouvement");

				// On affiche dans la console le thread qui execute le code
				// suivant
				displayUtils("D�placement d'un lapin...");

				// L'animal qui se trouve dans la position 1 (le lapin dans
				// notre
				// cas)
				janglePan.choisirAnimal(1);

				// executer dans un nouveau thread le traitement long qu'est le
				// d�placement d'un animal
				doLongTask();
			}

		});

		// Cr�ation d'un paneau qui va contenir les boutons de l'application
		JPanel menuPan = new JPanel();
		// ajout d boutons dans le paneau menu
		menuPan.add(btnLapin);
		menuPan.add(btnLion);

		// le paneau menu est ajout� au nord de la fentre
		add(menuPan, BorderLayout.NORTH);

		// Paneau qui affiche l'�tat de l'application
		JPanel statePan = new JPanel();
		statePan.add(stateLabel);
		add(statePan, BorderLayout.SOUTH);

		// ajout du paneau principal sur la fentre
		add(janglePan);

		// afficher la fentre
		setVisible(true);

	}

	/**
	 * M�thode permettant de d�placer l'animal
	 */
	public void doLongTask() {

		// on cr�e un thread pour executer la tache longue
		new Thread(new Runnable() {

			// La tache du thread
			public void run() {

				// On affiche dans la console le thread qui execute le code
				// suivant
				displayUtils("Execution de la tache longue...");

				Animal animal = janglePan.getAnimal();

				// Parcourir la liste des position de l'animal
				while (animal.notEnd()) {

					// On d�place l'animal et on r�cup�re la dur�e entre deux
					// positions successives
					int speed = janglePan.deplacer();

					// la majorit� des m�thodes qui manipulent l'inetrface
					// graphique ne doivent pas etre appel�es dans un thread
					// autre que l'EDT (Event Dispatch Thread) Pour appeler une
					// m�thode dans l'EDT on
					// peut utiliser les
					// m�thodes de SwingUtilities par exemple
					// Les m�thodes suivantes font l'exception elles peuvent
					// etre appel�es en dehors de l'EDT : repaint(),
					// revalidate(), invalidate().

					// Ainsi il est safe d'appeler repaint ici bien qu'on est
					// pas dans l'EDT
					repaint();

					// retrader l'execution pour simuler la vitesse de
					// d�placement
					try {
						Thread.sleep(speed * 100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				// On affiche dans l'interface l'�tat : ATTENTION il faut faire
				// �a dans l'EDT et non pas dans le thread en cours
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {

						// On affiche dans la console le thread qui execute le
						// code
						// suivant
						displayUtils("Modification de l'�tat de l'application");

						stateLabel.setText("Fin du mouvement de l'animal");

					}
				});
			}
		}, "Thread Long Task").start(); // on d�marre le thread
	}

	public static void displayUtils(String pMsg) {
		Thread currentThread = Thread.currentThread();

		System.out.println("L'op�ration " + pMsg
				+ " s'execute dans le thread  : " + currentThread.getName());
	}

	public static void main(String[] args) {
		displayUtils("d�but m�thode main");

		// On cr�er la fentre dans l'EDT (Event Dispatch Thread)

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				displayUtils("Cr�ation de la fentre");

				new MainFrame();
			}
		});

	}

}
