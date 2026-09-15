package view;
import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;


import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;



import controller.GameController;


import model.Giocatore;
import model.ModalitàAPunti;

import model.Partita;

/**
 * Rappresenta il pannello di fine partita.
 * Questa interfaccia si occupa di mostrare il vincitore assoluto (in modalità Classica)
 * oppure i punteggi ordinati dei vari giocatori (in modalità a Punti).
 * Fornisce inoltre i controlli per tornare al menu,visualizzare lo storico,
 * andare al round successivo o avviare una nuova partita.
 */
public class PannelloClassifica extends JPanel{
	
	private GameController controller;
	//label
	private JLabel lblNumeroGiocatori = new JLabel("Giocatori :");
	//button
    private JButton btnMenu = new JButton("Menu");
    private JButton btnStorico = new JButton("Storico");
    private JButton btnNuovaPartita = new JButton("Nuova Partita");
    private JButton btnContinuaPartita = new JButton("Continua Partita");
	// JPanel centrale come campo, poiché ogni volta dovremmo andare pulire il pannello e riscriverci sopra
    private JPanel pannelloCentrale = new JPanel(new GridBagLayout());
	
    /**
     * Costruttore della classe PannelloClassifica.
     * Imposta il layout principale, i colori di sfondo, i margini e richiama
     * i metodi per la creazione e la disposizione grafica delle varie sezioni del pannello.
     * Infine, attiva i listener per i bottoni.
     * @param controller Il {@link GameController} utilizzato per gestire
     * le schermate e la logica di navigazione.
     */
	public PannelloClassifica(GameController controller ) {
		
		setLayout(new BorderLayout()); 
		setBackground(FinestraPrincipale.AZZURRINO);
        // aggiunta di margine per non attaccare le scritte ai bordi del monitor
		setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // up, sx ,down, dx
		
		this.controller = controller;
		creaPannelloNord();
		creaPannelloCentro();
		creaPannelloSud();
		attivaListener();
	}
	
	/**
     * Crea e configura il pannello superiore (Nord) dell'interfaccia.
     * Contiene il numero dei giocatori sulla sinistra, il titolo "CLASSIFICA" al centro 
     * e il pulsante per tornare al Menu principale sulla destra.
     */
	private void creaPannelloNord() {
		
		//utilizzo il BorderLayout per distribuire al meglio le label
        JPanel pannelloNord = new JPanel(new BorderLayout());
        pannelloNord.setOpaque(false); // sempre trasparente
        
        // label giocatori SX
        lblNumeroGiocatori.setFont(FinestraPrincipale.FONT_TITOLI);
        lblNumeroGiocatori.setForeground(Color.WHITE);
        pannelloNord.add(lblNumeroGiocatori, BorderLayout.WEST);
        
        // label centro
        JLabel lblClassifica = new JLabel("CLASSIFICA");
        lblClassifica.setFont(FinestraPrincipale.FONT_TITOLI_30);
        lblClassifica.setHorizontalAlignment(SwingConstants.CENTER); // Centra il testo esattamente in mezzo
        lblClassifica.setForeground(Color.WHITE);
        pannelloNord.add(lblClassifica, BorderLayout.CENTER);
        
        //bottone menu
        btnMenu.setFont(FinestraPrincipale.FONT_TITOLI);
        pannelloNord.add(btnMenu, BorderLayout.EAST);
        // aggancio tutto al pannelloTavolo inserendo tutto a nord
        add(pannelloNord, BorderLayout.NORTH);
	}
	
	/**
     * Crea e configura il pannello centrale dell'interfaccia.
     * Utilizza un GridBagLayout come contenitore vuoto e trasparente. 
     * Questo pannello è dinamico e si aggiorna automaticamente, tramite il
     * metodo {@link #aggiorna(Partita)} alla fine di ogni partita o round.
     */
	private void creaPannelloCentro() {
        pannelloCentrale.setOpaque(false); // sfondo trasparente
        // aggiungo il pannelloCentrale al centro del PannelloClassifica
        add(pannelloCentrale, BorderLayout.CENTER);
    }
	
	/**
     * Crea e configura il pannello inferiore (Sud) della classifica.
     * Utilizza un GridBagLayout per l'allineamento dei bottoni
     * (Storico, Nuova Partita, Continua Partita e Rigioca).
     */
	private void creaPannelloSud() {
		
        JPanel pannelloSud = new JPanel(new GridBagLayout());
    	pannelloSud.setOpaque(false); // sempre trasparente
    	GridBagConstraints gbc = new GridBagConstraints();
    	gbc.insets = new Insets(20,30,20,30); // gestisce gli spazi tra i componenti (up, sx, down, dx) 
    	
    	//btn storico
    	btnStorico.setFont(FinestraPrincipale.FONT_TITOLI);
    	pannelloSud.add(btnStorico, gbc);
    	
    	//btn NuovaPartita
    	btnNuovaPartita.setFont(FinestraPrincipale.FONT_TITOLI);
    	pannelloSud.add(btnNuovaPartita,gbc);
    	
    	///btnContinuaPartita
    	btnContinuaPartita.setFont(FinestraPrincipale.FONT_TITOLI);
    	pannelloSud.add(btnContinuaPartita,gbc);
    	
    	add(pannelloSud, BorderLayout.SOUTH);
    	
	}
	
	/**
	 * Attiva i listener dei vari pulsanti presenti nel pannello.
	 * Ogni bottone delega la logica al GameController.
	 */
	private void attivaListener() {
	
		// listener pulsante esci
	    this.btnMenu.addActionListener(e -> controller.mostraMenu());
	    
	    //listener pulsante storico
	    this.btnStorico.addActionListener(e -> controller.mostraStorico());
	    
	    //listener pulsante nuova partita
	    this.btnNuovaPartita.addActionListener(e -> controller.mostraConfigurazione());
	    
	    //listener pulsante Continua Partita
	    this.btnContinuaPartita.addActionListener(e -> controller.avvioNuovoRound());
	    
	}
	
	/**
     * Aggiorna dinamicamente l'interfaccia grafica del pannello in base allo stato attuale della partita.
     * Svuota il pannello centrale e lo ricostruisce mostrando:
     * In Modalità a Punti: La soglia di vittoria e la classifica dei giocatori ordinata per punteggio.
     * In Modalità Classica: Il nome del vincitore assoluto della singola partita.
     * Inoltre, gestisce in automatico la visibilità dei bottoni di navigazione (Continua Partita, Rigioca, Nuova Partita) 
     * @param partita, l'oggetto {@link Partita} contenente le informazioni aggiornate sui giocatori, i punteggi, 
     * il vincitore e la modalità di gioco in corso.
     */
	public void aggiorna(Partita partita) {
		
		// prendo i dati da mostrare
		int numGiocatori = partita.getNumGiocatori();
		lblNumeroGiocatori.setText("Giocatori Totali : " + numGiocatori);
		
		pannelloCentrale.removeAll();
		
		// creo il GridBagLayout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(15, 20, 15, 20); // Margini leggermente ridotti per compattezza
		gbc.gridy = 0;
		
		// se siamo in modalità a punti
		if (partita.isModalitaAPunti()) {
			
			// faccio il downcast per ottenere i punti vittoria
			int punteggioVincita = ((ModalitàAPunti) partita.getModalita()).getPuntiVittoria();
			JLabel lblPunti = new JLabel("Soglia Vittoria: " + punteggioVincita + " pt");
			lblPunti.setFont(FinestraPrincipale.FONT_TITOLI);
			lblPunti.setForeground(Color.WHITE); 
			
			pannelloCentrale.add(lblPunti, gbc);
			
			// Utilizzo uno stream per riordinare in ordine di punteggio l'array giocatori
			Giocatore[] giocatori = java.util.Arrays.stream(partita.getGiocatori())
					.sorted((g1, g2) -> Integer.compare(g2.getPunti(), g1.getPunti()))
					.toArray(Giocatore[]::new);

			// ciclo sull'array per stampare nome, posizione e punteggio
			for (int i = 0; i < numGiocatori; i++) {
				JLabel lblGiocatori = new JLabel((i + 1) + "° " + giocatori[i].getNome() + " | Punti: " + giocatori[i].getPunti());
				lblGiocatori.setFont(FinestraPrincipale.FONT_TITOLI_30);
				lblGiocatori.setForeground(Color.WHITE);
				gbc.gridy++; // scendo di una riga
				
				pannelloCentrale.add(lblGiocatori, gbc);
			}
			
			// vedo se esiste un vincitore totale
			boolean vittoriaFinale = partita.getModalita().win(partita.getVincitore()) != null;
			//logica bottoni
			// se c'è vittoriaFinale, nascondo "Continua" e mostro "NuovaPartita"
			btnContinuaPartita.setVisible(!vittoriaFinale);
			btnNuovaPartita.setVisible(vittoriaFinale);
			
		} else {
			
			// MODALITÀ CLASSICA
			Giocatore vincitore = partita.getVincitore(); 
			JLabel lblVincitore = new JLabel("Vincitore: " + vincitore.getNome());
			lblVincitore.setFont(FinestraPrincipale.FONT_TITOLI_30);
			lblVincitore.setForeground(Color.WHITE);
			
			gbc.anchor = GridBagConstraints.CENTER; // in classica centriamo il testo
			pannelloCentrale.add(lblVincitore, gbc);		    	
			
			// gestione bottoni
			btnContinuaPartita.setVisible(false);
			btnNuovaPartita.setVisible(true);
		}
		
		// ricarico la grafica del pannello centrale con i nuovi elementi
		pannelloCentrale.revalidate();
		pannelloCentrale.repaint();
	}
}

	
	
	