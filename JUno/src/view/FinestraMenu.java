package view;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.GameController;
import utility.GestoreAsset;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Rappresenta il pannello del Menu Principale dell'applicazione JUno.
 * Questa schermata accoglie l'utente all'avvio del gioco e fornisce
 * i pulsanti per avviare una nuova partita, accedere alle impostazioni 
 * o uscire dall'applicazione.
 */
public class FinestraMenu extends JPanel {
	
	private GameController controller;

	private JLabel lblLogo = new JLabel();
    private JButton btnGioca = new JButton("GIOCA");
    private JButton btnImpostazioni = new JButton("IMPOSTAZIONI");
    private JButton btnEsci = new JButton("ESCI");
    
    /**
     * Costruttore della classe FinestraMenu.
     * Imposta il colore di sfondo, disegna i componenti grafici.
     * e collega i listener per gestire i click dell'utente.
     * @param controller, il {@link GameController} utilizzato per la navigazione tra i pannelli.
     */
	public FinestraMenu(GameController controller) {
		
		this.controller = controller;
		setBackground(FinestraPrincipale.AZZURRINO);
		creaBottoni();	
		attivaListener();
	}

	/**
     * Inizializza, posiziona e formatta i componenti grafici all'interno del pannello.
     * Sfrutta il {@link GridBagLayout} per centrare il logo e i bottoni, 
     * applicando font e dimensioni.
     */
	private void creaBottoni() {
		
		// utilizzo il GridBagLayout su questo pannello per tenere i pulsanti uno sotto l'altro
				this.setLayout(new GridBagLayout());
				
		        GestoreAsset gestore = GestoreAsset.getInstance();        
		        java.awt.image.BufferedImage imgLogo = gestore.getImmagine("JUNO"); 
		        
		        if (imgLogo != null) {
		            // scala l'immagine per renderla piu bella
		            java.awt.Image imgScalata = imgLogo.getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH);
		            lblLogo.setIcon(new ImageIcon(imgScalata));
		        } else {
		            // se l'immagine non viene trovata
		            lblLogo.setText("JUNO");
		            lblLogo.setFont(FinestraPrincipale.FONT_TITOLI_30);
		        }

		        ///aggiungo il logo
		        this.add(lblLogo);
				// ingrandimento del testo dei bottoni
				btnGioca.setFont(FinestraPrincipale.FONT_TITOLI);
				btnImpostazioni.setFont(FinestraPrincipale.FONT_TITOLI);
				btnEsci.setFont(FinestraPrincipale.FONT_TITOLI);
				
				// utilizzo del GridBagConstraints
		        GridBagConstraints gbc = new GridBagConstraints();
				// va a capo dopo ogni bottone
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				 // spaziatura, 20 pixel di spazio sopra e sotto ogni bottone
				gbc.insets = new Insets(20, 0, 20, 0);
				//  allarga tutti i bottoni alla stessa larghezza
				gbc.fill = GridBagConstraints.HORIZONTAL;
				// ingrandisce un po' i bottoni
				gbc.ipadx = 100; 
				gbc.ipady = 20;
				
				// aggiungo i bottoni con tutte le modifiche effettuate
				this.add(btnGioca, gbc);
				this.add(btnImpostazioni,gbc);
				this.add(btnEsci, gbc);
	}
	
	/**
     * attiva i listener dei vari button presenti nel menu.
     * Ogni bottone delega la logica al GameController.
     */
	private void attivaListener() {
		
		btnGioca.addActionListener(e -> controller.mostraConfigurazione());
		btnImpostazioni.addActionListener(e -> controller.mostraImpostazioni(false));
	    btnEsci.addActionListener(e -> controller.esciDalGioco());
	}
}