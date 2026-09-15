package view;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.security.PrivateKey;

import controller.GameController;
import model.Partita;
import utility.GestoreAsset; 

/**
 * Classe principale che rappresenta la finestra dell'applicazione JUno.
 * Estende {@link JFrame} e funge da contenitore principale per tutte le schermate del gioco.
 * Utilizza un {@link CardLayout} per gestire la navigazione e lo scambio dinamico 
 * dei vari pannelli senza dover aprire nuove finestre.
 */
public class FinestraPrincipale extends JFrame {
    
    private GameController controller;
    private CardLayout cardLayout;
    private JPanel pannelloContenitore; 

    // TUTTI I PANNELLI DEL GIOCO
    private FinestraMenu pannelloMenu;
    private PannelloConfigurazione pannelloConfigurazione;
    private PannelloNomi pannelloNomi;
    private PannelloTavolo pannelloTavolo; 
    private PannelloClassifica pannelloClassifica;
    private PannelloStorico pannelloStorico;
    private PannelloImpostazioni pannelloImpostazioni;
    
    //vari font e colori utilizzabili da tutti i panel
    public static final Font FONT_TITOLI = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_TITOLI_30 = new Font("SansSerif", Font.BOLD, 30);
    public static final Font FONT_NORMALE = new Font("Tahoma", Font.PLAIN, 18);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 15);
    public static final Color AZZURRINO = new Color(75, 105, 130); 
    public static final Color VERDE = new Color(1, 68, 33);
    
    /**
     * Costruttore della finestra principale.
     * Inizializza le impostazioni grafiche di base (dimensioni, icona, chiusura), 
     * il layout contenitore e istanzia tutti i pannelli del gioco inserendoli nel CardLayout.
     * @param controller, il {@link GameController} che gestisce la logica e le interazioni.
     */
    public FinestraPrincipale(GameController controller) {
        this.controller = controller;
        
        setTitle("JUno");
        
        // metto il logo del gioco
        GestoreAsset gestore = GestoreAsset.getInstance();
        java.awt.image.BufferedImage icona = gestore.getImmagine("JUNO"); 
        if (icona != null) {
            setIconImage(icona);
        }
        
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setMinimumSize(new Dimension(1024, 768));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la finestra sullo schermo
        
         
        this.cardLayout = new CardLayout();
        this.pannelloContenitore = new JPanel(cardLayout);
        
        // INIZIALIZZAZIONE PANNELLI
        this.pannelloMenu = new FinestraMenu(controller);
        this.pannelloImpostazioni = new PannelloImpostazioni(controller);
        this.pannelloConfigurazione = new PannelloConfigurazione(controller);
        this.pannelloNomi = new PannelloNomi(controller); 
        this.pannelloTavolo = new PannelloTavolo(controller); 
        this.pannelloClassifica = new PannelloClassifica(controller);
        this.pannelloStorico = new PannelloStorico(controller);
        
         
        // INSERIMENTO NEL CARDLAYOUT
        pannelloContenitore.add(this.pannelloMenu, "MENU");
        pannelloContenitore.add(this.pannelloImpostazioni, "IMPOSTAZIONI");
        pannelloContenitore.add(this.pannelloConfigurazione, "CONFIGURAZIONE");
        pannelloContenitore.add(this.pannelloNomi, "NOMI"); 
        pannelloContenitore.add(this.pannelloTavolo, "TAVOLO");
        pannelloContenitore.add(this.pannelloClassifica, "CLASSIFICA");
        pannelloContenitore.add(this.pannelloStorico, "STORICO");
         
        this.add(pannelloContenitore);
        setVisible(true);
    }
    
    /** Mostra la schermata del Menu Principale. */
    public void mostraMenu() {
        cardLayout.show(pannelloContenitore, "MENU");
    }
    
    /** Mostra la schermata per la configurazione delle regole e dei giocatori. */
    public void mostraConfigurazione() {
        cardLayout.show(pannelloContenitore, "CONFIGURAZIONE");
    }
    
    /** Mostra la schermata delle impostazioni */
    public void mostraImpostazioni() {
        cardLayout.show(pannelloContenitore, "IMPOSTAZIONI");
    }
    
    /** Mostra la schermata per l'inserimento dei nomi dei giocatori. */
    public void mostraNomi() {
        cardLayout.show(pannelloContenitore, "NOMI");
    }
    
    /** Mostra il tavolo da gioco principale. */
    public void mostraTavolo() {
        cardLayout.show(pannelloContenitore, "TAVOLO"); 
    }
    
    /**
     * Mostra la schermata della classifica aggiornandola con i dati più recenti.
     * @param partitaFinita, oggetto {@link Partita} contenente lo stato finale e i punteggi del round.
     */
    public void mostraClassifica(Partita partitaFinita) {
        pannelloClassifica.aggiorna(partitaFinita);
        cardLayout.show(pannelloContenitore, "CLASSIFICA");
    }
    
    /**
     * Mostra la schermata dello storico delle partite giocate.
     * @param partita, oggetto {@link Partita} utilizzato per estrarre i dati dallo storico.
     */
    public void mostraStorico(Partita partita) {
        pannelloStorico.aggiorna(partita);
        cardLayout.show(pannelloContenitore, "STORICO");
    }
    
    // --GETTER PER IL CONTROLLER-- 
    
    /** @return Il pannello del Menu Principale. */
    public FinestraMenu getFinestraMenu() {
        return this.pannelloMenu;
    }
    
    /** @return Il pannello di Configurazione. */
    public PannelloConfigurazione getPannelloConfigurazione() {
        return this.pannelloConfigurazione;
    }
    
    /** @return Il pannello di inserimento Nomi. */
    public PannelloNomi getPannelloNomi() {
        return this.pannelloNomi;
    }
    
    /** @return Il pannello del Tavolo da gioco. */
    public PannelloTavolo getPannelloTavolo() {
        return this.pannelloTavolo;
    }
    
    /** @return Il pannello delle Impostazioni. */
    public PannelloImpostazioni getPannelloImpostazioni() {
        return this.pannelloImpostazioni;
    }
}