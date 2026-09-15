package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JPanel;
import controller.GameController;

/**
 * Pannello della View che rappresenta il menu delle impostazioni/pausa.
 * Mostra i pulsanti per salvare la partita, caricarne una precedente, 
 * tornare al gioco in corso o uscire verso il menu principale.
 * L'aspetto e la visibilità dei bottoni cambiano dinamicamente 
 * in base a dove viene aperto il pannello (dal menu o durante una partita).
 */
public class PannelloImpostazioni extends JPanel {
	private GameController controller;
	private JButton btnSalva = new JButton("Salva Partita");
	private JButton btnCarica = new JButton("Carica Partita");
	private JButton btnRiprendiPartita = new JButton("Riprendi Partita");
	private JButton btnTornaMenu = new JButton("Torna al Menu");
	
	/**
	 * Costruttore del PannelloImpostazioni.
	 * Inizializza l'interfaccia grafica del menu di pausa.
	 * @param controller, il {@link GameController} per gestire le azioni di salvataggio/caricamento e navigazione.
	 */
	public PannelloImpostazioni (GameController controller) {
		this.controller = controller;
		this.setBackground(FinestraPrincipale.AZZURRINO);
		creaBottoni();
		attivaListener();
	}
	
	/**
	 * Instanzia, formatta e posiziona i bottoni all'interno del pannello
	 * utilizzando un GridBagLayout per mantenerli impilati verticalmente e centrati.
	 */
	private void creaBottoni() {
		this.setLayout(new GridBagLayout());

		btnSalva.setFont(FinestraPrincipale.FONT_TITOLI);
		btnCarica.setFont(FinestraPrincipale.FONT_TITOLI);
		btnTornaMenu.setFont(FinestraPrincipale.FONT_TITOLI);
		btnRiprendiPartita.setFont(FinestraPrincipale.FONT_TITOLI);
		
		GridBagConstraints gbc = new GridBagConstraints();
		// va a capo dopo ogni bottone
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		// 20 pixel di spazio sopra e sotto ogni bottone
		gbc.insets = new Insets(20, 0, 20, 0);
		// allarga tutti i bottoni alla stessa larghezza
		gbc.fill = GridBagConstraints.HORIZONTAL;
		// ingrandisce la dimensione cliccabile dei bottoni
		gbc.ipadx = 100; 
		gbc.ipady = 20;  
		
		this.add(btnRiprendiPartita, gbc);
		this.add(btnCarica, gbc);
		this.add(btnSalva, gbc);
		this.add(btnTornaMenu, gbc);
	}
	/**
	 * Accende o spegne i bottoni Salva/Carica e Riprendi in base al contesto 
	 * in cui viene aperto il pannello (in partita o dal menu principale).
	 * @param dalTavolo {@code true} se siamo in partita, abilita Salva e Riprendi, 
	 * {@code false} se siamo nel menu, abilita solo Carica e torna al menu.
	 */
	public void gestisciBottoni(boolean dalTavolo) {
		if (dalTavolo) {
			btnSalva.setVisible(true);           
			btnCarica.setVisible(false);        
			btnRiprendiPartita.setVisible(true); 
		} else {
			btnSalva.setVisible(false);          
			btnCarica.setVisible(true);         
			btnRiprendiPartita.setVisible(false); 
		}
	}
	/**
	 * Associa i listener ai bottoni per comunicare con il controller.
	 */
	private void attivaListener() {
		btnCarica.addActionListener(e -> controller.caricaPartita()); 
        btnSalva.addActionListener(e -> controller.salvaPartita());
		
		// torna indietro rimuovendo la pausa
		btnRiprendiPartita.addActionListener(e -> controller.chiudiImpostazioni());
		
		// distrugge la partita, toglie la pausa e torna al menu principale
		btnTornaMenu.addActionListener(e -> controller.mostraMenu());
	}
}