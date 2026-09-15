package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import controller.GameController;

/**
 * Pannello della View in cui l'utente inserisce i nomi per i giocatori.
 * Genera dinamicamente le caselle di testo in base al numero di umani e bot scelti
 * nel PannelloConfigurazione.
 */
public class PannelloNomi extends JPanel {
    
    private GameController controller;
    private JPanel pannelloCampi = new JPanel(new GridBagLayout()); 
    private JButton btnIndietro = new JButton("INDIETRO");
    private JButton btnAvvia = new JButton("AVVIA PARTITA");
    // lista per tenere traccia delle JTextField create dinamicamente
    private List<JTextField> listaCampiNomi = new ArrayList<>(); 
    private List<JComboBox<String>> listaDifficoltaBot = new ArrayList<>();
 

    /**
     * Costruttore del PannelloNomi.
     * @param controller, il {@link GameController} per gestire la navigazione e l'avvio della partita.
     */
    public PannelloNomi(GameController controller) {
        this.controller = controller;
        // layout principale
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // spaziatura (up, sx, down, dx)
        
        creaTitolo(gbc);
        creaSezioneNomi(gbc);  
        creaBottoni(gbc);
        attivaListener();
    }
    
    /**
     * Crea e posiziona il titolo del pannello.
     * @param gbc, le impostazioni di layout correnti.
     */
    private void creaTitolo(GridBagConstraints gbc) {
        gbc.gridy = 0; gbc.gridx = 0;
        JLabel lblTitolo = new JLabel("INSERISCI I NOMI DEI GIOCATORI", SwingConstants.CENTER);
        lblTitolo.setFont(FinestraPrincipale.FONT_TITOLI); 
        add(lblTitolo, gbc);
    }
    
    /**
     * Crea un pannello vuoto sotto il titolo che verrà poi riempito dinamicamente.
     * Questo approccio evita che label e textfield si posizionino in maniera anomala nello schermo.
     * @param gbc, le impostazioni di layout correnti.
     */
     private void creaSezioneNomi(GridBagConstraints gbc) {
        gbc.gridy++;
        add(pannelloCampi, gbc);
    }
    
    /**
     * Crea e posiziona i bottoni di navigazione in basso.
     * @param gbc, le impostazioni di layout correnti.
     */
    private void creaBottoni(GridBagConstraints gbc) {
        gbc.gridy++;
        JPanel pannelloBottoni = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        btnIndietro.setFont(FinestraPrincipale.FONT_TITOLI);
        btnAvvia.setFont(FinestraPrincipale.FONT_TITOLI);
        pannelloBottoni.add(btnIndietro);
        pannelloBottoni.add(btnAvvia);
        add(pannelloBottoni, gbc);
    }
    
    /**
     * Viene chiamato dal Controller per generare graficamente le caselle di testo
     * necessarie per l'inserimento dei nomi di umani e bot e le caselle di selezione
     * della modalità unicamente per i bot.
     * @param numUmani, il numero di giocatori umani selezionati.
     * @param numBot, il numero di bot selezionati.
     */
    public void generaCampiNomi(int numUmani, int numBot) {
    	//pulisco tutti i vecchi campi
        pannelloCampi.removeAll(); 
        listaCampiNomi.clear();
        listaDifficoltaBot.clear();
        
        // utilizzo di un nuovo GridBagConstraints dedicato solo all'interno della scatola dei nomi
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 0;
        
        // genera i campi per gli umani
        for (int i = 1; i <= numUmani; i++) {
            gbc.gridx = 0;
            JLabel lbl = new JLabel("Umano " + i + ":");
            lbl.setFont(FinestraPrincipale.FONT_NORMALE);
            pannelloCampi.add(lbl, gbc);
            gbc.gridx = 1;
            JTextField txtNome = new JTextField("Umano " + i, 15);
            txtNome.setFont(FinestraPrincipale.FONT_NORMALE);
            pannelloCampi.add(txtNome, gbc);
            listaCampiNomi.add(txtNome);
            gbc.gridy++;
        }
        // genera i campi per i bot
        for (int i = 1; i <= numBot; i++) {
        
            gbc.gridx = 0;
            JLabel lbl = new JLabel("Bot " + i + ":");
            lbl.setFont(FinestraPrincipale.FONT_NORMALE);
            pannelloCampi.add(lbl, gbc);
        
            gbc.gridx = 1;
            JTextField txtNome = new JTextField("Bot " + i, 15);
            txtNome.setFont(FinestraPrincipale.FONT_NORMALE);
            pannelloCampi.add(txtNome, gbc);
            listaCampiNomi.add(txtNome);
            
            gbc.gridx = 2;
            JComboBox<String> comboDifficoltaBot = new JComboBox<>(new String[]{"Casuale","Aggressivo","Difensivo"});
            comboDifficoltaBot.setFont(FinestraPrincipale.FONT_NORMALE);
            pannelloCampi.add(comboDifficoltaBot, gbc);
            listaDifficoltaBot.add(comboDifficoltaBot); 
            
            gbc.gridy++;
        }
        //aggiorna graficamente il pannello dopo aver aggiunto tutto
        pannelloCampi.revalidate();
        pannelloCampi.repaint();
    }
    
    /**
     * Estrae e memorizza i nomi inseriti dall'utente scorrendo le TextFields.
     * Gestisce la validazione, se un campo è vuoto, mostra un errore e interrompe l'operazione.
     * @return nomi, un array di String con i nomi dei giocatori, oppure null se la validazione fallisce.
     */
    public String[] estraiNomi() {
        String[] nomi = new String[listaCampiNomi.size()];
        for (int i = 0; i < listaCampiNomi.size(); i++) {
            nomi[i] = listaCampiNomi.get(i).getText().trim();
            if (nomi[i].isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                        "Devi inserire un nome per ogni giocatore.", 
                        "Nome Mancante", 
                        JOptionPane.WARNING_MESSAGE);
                return null; 
            }
        }
        return nomi;
    }
    
    
    /**
     * Estrae le difficoltà selezionate per ogni bot scorrendo le relative tendine.
     * @return difficolta, un array di String con le difficoltà.
     */
    public String[] estraiDifficoltaBot() {
        String[] difficolta = new String[listaDifficoltaBot.size()];
        for (int i = 0; i < listaDifficoltaBot.size(); i++) {
            difficolta[i] = (String) listaDifficoltaBot.get(i).getSelectedItem();
        }
        return difficolta;
    }
    
    /**
     * Associa i listener ai bottoni collegandoli alle direttive del {@link GameController}.
     */
    private void attivaListener() {
        // ritorna alla schermata di configurazione
        btnIndietro.addActionListener(e -> controller.mostraConfigurazione());
        
        // avvia la fase di raccoglimento dati e inizio partita
        btnAvvia.addActionListener(e -> controller.avviaPartita());
    }

    // --- GETTER ---
    
    /** Restituisce il bottone per tornare indietro. 
     * @return btnIndietro. 
     */
    public JButton getBtnIndietro() { return btnIndietro; }
    
    /** Restituisce il bottone per avviare il gioco. 
     * @return btnAvvia. 
     */
    public JButton getBtnAvviaVero() { return btnAvvia; }
}