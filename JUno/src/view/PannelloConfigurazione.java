package view;

import javax.swing.*;
import controller.GameController;
import java.awt.*;

/**
 * Pannello della View dedicato alla configurazione iniziale della partita.
 * Permette all'utente di scegliere la modalità di gioco (Classica o a Punti),
 * attivare regole alternative (Stacking, Number Rush, Solo Bot) e 
 * definire il numero e la difficoltà dei giocatori (umani e bot).
 */
public class PannelloConfigurazione extends JPanel {
	
    private GameController controller;

    // JRadioButton per la gestione della modalità della partita
    private JRadioButton radioClassica = new JRadioButton("Classica", true);
    private JRadioButton radioPunti = new JRadioButton("A Punti");
    
    // Checkbox per le regole aggiuntive
    private JCheckBox checkSoloBot = new JCheckBox("Modalità Solo Bot");
    private JCheckBox checkStacking = new JCheckBox("Stacking Carte");
    private JCheckBox checkNumberRush = new JCheckBox("Number Rush");
    
    // JComboBox per giocatori, punti e difficoltà
    private JComboBox<Integer> comboUmani = new JComboBox<>(new Integer[]{0, 1, 2, 3, 4, 5, 6});
    private JComboBox<Integer> comboBot = new JComboBox<>(new Integer[]{0, 1, 2, 3, 4, 5, 6});
    private JComboBox<Integer> comboPuntiVittoria = new JComboBox<>(new Integer[]{250, 500, 750, 1000});
    
    // bottoni di navigazione
    private JButton btnAvanti = new JButton("AVANTI"); 
    private JButton btnMenu = new JButton("INDIETRO");

    /**
     * Costruttore del PannelloConfigurazione.
     * Imposta il layout e richiama metodi, per la costruzione dei vari moduli della schermata.
     * @param controller, il GameController per gestire le azioni dei bottoni.
     */
    public PannelloConfigurazione(GameController controller) {
        this.controller = controller;
        // impostazioni base del layout GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // gestisce gli spazi tra i componenti (up, sx, down, dx) 
        
        creaModalitaGioco(gbc);
        creaRegoleAlternative(gbc);
        creaImpostazioniGiocatori(gbc);
        creaBottoniNavigazione(gbc);
        attivaListener();
    }
    
    /**
     * Crea e posiziona la sezione relativa alla scelta della modalità di gioco.
     */
    private void creaModalitaGioco(GridBagConstraints gbc) {
        // riga 0
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 0;
        
        JLabel lblModalita = new JLabel("MODALITÀ VITTORIA", SwingConstants.CENTER);
        lblModalita.setFont(FinestraPrincipale.FONT_TITOLI);
        add(lblModalita, gbc);

        radioClassica.setFont(FinestraPrincipale.FONT_NORMALE);
        radioPunti.setFont(FinestraPrincipale.FONT_NORMALE);
        
        // utilizzo un ButtonGroup affinché sia selezionabile solo uno dei due bottoni
        ButtonGroup gruppoModalita = new ButtonGroup();
        gruppoModalita.add(radioClassica);
        gruppoModalita.add(radioPunti);
        
        JLabel lblPuntiVittoria = new JLabel("|  Punti Vittoria:");
        lblPuntiVittoria.setFont(FinestraPrincipale.FONT_LABEL);
        
        // combo per i punti vittoria (attivo solo se "A Punti" è selezionato)
        comboPuntiVittoria.setPreferredSize(new Dimension(80, 40)); 
        comboPuntiVittoria.setSelectedItem(500); // valore di default
        comboPuntiVittoria.setEnabled(false);    // inizialmente disabilitato
        
        // pannello contenitore
        JPanel panelModalita = new JPanel();
        panelModalita.add(radioClassica);
        panelModalita.add(radioPunti);   
        panelModalita.add(lblPuntiVittoria);
        panelModalita.add(comboPuntiVittoria); 
        
        gbc.gridy++; // scende alla riga 1
        add(panelModalita, gbc);
    }
    
    /**
     * Crea e posiziona la sezione per l'attivazione delle regole alternative.
     */
    private void creaRegoleAlternative(GridBagConstraints gbc) {
        // Riga 2 
        gbc.gridy++; 
        
        JLabel lblRegole = new JLabel("REGOLE ALTERNATIVE", SwingConstants.CENTER);
        lblRegole.setFont(FinestraPrincipale.FONT_TITOLI);
        add(lblRegole, gbc);

        checkStacking.setFont(FinestraPrincipale.FONT_NORMALE);
        checkNumberRush.setFont(FinestraPrincipale.FONT_NORMALE);
        checkSoloBot.setFont(FinestraPrincipale.FONT_NORMALE);

        JPanel panelRegole = new JPanel();
        panelRegole.add(checkStacking);
        panelRegole.add(checkNumberRush);
        panelRegole.add(checkSoloBot);
        
        gbc.gridy++; // scende alla riga 3
        add(panelRegole, gbc);
    }
    
    /**
     * Crea e posiziona la sezione relativa alla configurazione di giocatori umani e bot.
     */
    private void creaImpostazioniGiocatori(GridBagConstraints gbc) {
        // riga 4 
        gbc.gridy++; 
        
        JLabel lblGiocatori = new JLabel("IMPOSTAZIONI GIOCATORI", SwingConstants.CENTER);
        JLabel lblNumeroPlayer = new JLabel("Massimo 6 Player");
        lblGiocatori.setFont(FinestraPrincipale.FONT_TITOLI);
        lblNumeroPlayer.setFont(FinestraPrincipale.FONT_TITOLI);
        
        add(lblGiocatori, gbc);
        gbc.gridy++; // riga 5
        add(lblNumeroPlayer, gbc);
        
        // panel per i player con 20 pixel di distanza orizzontale tra i componenti
        JPanel panelPlayer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        // sezione Umani
        JLabel lblNumeroUmani = new JLabel("Umani :");
        lblNumeroUmani.setFont(FinestraPrincipale.FONT_LABEL);
        comboUmani.setPreferredSize(new Dimension(70, 40));
        comboUmani.setSelectedItem(1); 

        // sezione Bot
        JLabel lblNumeroBot = new JLabel("Bot :");
        lblNumeroBot.setFont(FinestraPrincipale.FONT_LABEL);
        comboBot.setPreferredSize(new Dimension(70, 40));
        comboBot.setSelectedItem(1); 
        
        // aggiungo tutto nel panel orizzontale
        panelPlayer.add(lblNumeroUmani);
        panelPlayer.add(comboUmani);
        panelPlayer.add(lblNumeroBot);
        panelPlayer.add(comboBot);

        
        gbc.gridy++; // scende alla riga 6
        add(panelPlayer, gbc);
    }
    
    /**
     * Crea e posiziona i bottoni "Indietro" e "Avanti".
     */
    private void creaBottoniNavigazione(GridBagConstraints gbc) {
        // riga 7
        gbc.gridy++;
        // pannello contenitore con 50 pixel di spaziatura orizzontale
        JPanel panelBtnNavigazione = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        btnMenu.setFont(FinestraPrincipale.FONT_TITOLI);
        btnAvanti.setFont(FinestraPrincipale.FONT_TITOLI);
        panelBtnNavigazione.add(btnMenu);
        panelBtnNavigazione.add(btnAvanti);
        add(panelBtnNavigazione, gbc);
    }
    
    /**
     * Metodo che definisce e attiva i vari listener dei componenti presenti.
     */
    private void attivaListener() {
        
        // gestione checkbox "Solo Bot"
        checkSoloBot.addActionListener(e -> {
            boolean isSoloBot = checkSoloBot.isSelected();
            comboUmani.setEnabled(!isSoloBot);    
            if (isSoloBot) {
                comboUmani.setSelectedItem(0);
                // garantisce almeno 2 giocatori se si gioca solo con i bot
                if ((Integer) comboBot.getSelectedItem() < 2) { 
                    comboBot.setSelectedItem(2);
                }
            } else if ((Integer) comboBot.getSelectedItem() == 6) { 
                // se togliamo "Solo bot" e avevamo 6 bot, scendiamo a 5 per fare spazio a 1 umano
                comboBot.setSelectedItem(5);
            }
        });
        
        // attiva la scelta dei punti solo quando si seleziona la modalità "A Punti"
        radioPunti.addItemListener(e -> {
            boolean isRadioPunti = radioPunti.isSelected();
            comboPuntiVittoria.setEnabled(isRadioPunti);
        });

        // azione Menu Principale
        btnMenu.addActionListener(e -> controller.mostraMenu());
        
        // azione avanti
        btnAvanti.addActionListener(e -> {
            int umani = (Integer) getComboUmani().getSelectedItem();
            int bot = (Integer) getComboBot().getSelectedItem();
            int totaleGiocatori = umani + bot;
              
            // controllo validità numero giocatori
            if (totaleGiocatori < 2 || totaleGiocatori > 6) {
                JOptionPane.showMessageDialog(this, 
                        "Il numero totale di giocatori deve essere compreso tra 2 e 6.",
                        "Numero di player non valido", 
                        JOptionPane.WARNING_MESSAGE);
            } else { 
                controller.mostraSchermataNomi();
            }
        });
        // attiva "Solo Bot" se si impostano 0 umani
        comboUmani.addActionListener(e -> {
            int numeroUmani = (Integer) comboUmani.getSelectedItem();
            
            if (numeroUmani == 0) {
                checkSoloBot.setSelected(true);   
                comboUmani.setEnabled(false);     
                if ((Integer) comboBot.getSelectedItem() < 2) {
                    comboBot.setSelectedItem(2); 
                }
            } else {
                checkSoloBot.setSelected(false);  
            }
        });

        // disattiva "Solo Bot" se si impostano 0 bot o se si riempiono i 6 slot bot
        comboBot.addActionListener(e -> {
            int numeroBot = (Integer) comboBot.getSelectedItem();
            if (numeroBot == 0) {
                checkSoloBot.setSelected(false); 
                comboUmani.setEnabled(true);   
                if ((Integer) comboUmani.getSelectedItem() < 2) {
                    comboUmani.setSelectedItem(2); 
                }
            } else if (numeroBot == 6) { 
                checkSoloBot.setSelected(true); 
                comboUmani.setSelectedItem(0);  
                comboUmani.setEnabled(false);   
            }
        });
    }
          
    // --- GETTER --- 
    
    /** Restituisce il bottone per la selezione della Modalità Classica. 
     * @return radioClassica. 
     */
    public JRadioButton getRadioClassica() { return radioClassica; }
    
    /** Restituisce il bottone per la selezione della Modalità a Punti. 
     * @return radioPunti. 
     */
    public JRadioButton getRadioPunti() { return radioPunti; }
    
    /** Restituisce la checkbox per l'attivazione della regola Stacking. 
     * @return checkStacking. 
     */
    public JCheckBox getCheckStacking() { return checkStacking; }
    
    /** Restituisce la checkbox per l'attivazione della regola Number Rush. 
     * @return checkNumberRush. 
     */
    public JCheckBox getCheckNumberRush() { return checkNumberRush; }
    
    /** Restituisce la checkbox per l'attivazione della simulazione "Solo Bot". 
     * @return checkSoloBot. 
     */
    public JCheckBox getCheckSimulazioneBot() { return checkSoloBot; }
    
    /** Restituisce il menu a tendina con il numero di giocatori umani selezionato. 
     * @return comboUmani. 
     */
    public JComboBox<Integer> getComboUmani() { return comboUmani; }
    
    /** Restituisce il menu a tendina con il numero di giocatori controllati dall'IA. 
     * @return comboBot. 
     */
    public JComboBox<Integer> getComboBot() { return comboBot; }
    
    /** Restituisce il menu a tendina con la soglia dei punti vittoria. 
     * @return comboPuntiVittoria. 
     */
    public JComboBox<Integer> getComboPuntiVittoria() { return comboPuntiVittoria; }
}
