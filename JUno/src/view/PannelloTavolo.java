package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import controller.GameController;
import model.Carta;
import model.Colori;
import model.Giocatore;
import model.Partita;
import utility.GestoreAsset;

/**
 * Rappresenta il pannello del tavolo da gioco dell'applicazione JUno.
 * Questa classe gestisce la visualizzazione della partita,
 * la mano del giocatore, la pila degli scarti, il mazzo di pesca, lo storico delle mosse,
 * i turni in corso e i menu a comparsa (scelta colori, challenge JUno).
 */
public class PannelloTavolo extends JPanel {
    
    private GameController controller;
    
    // impostazioni grafiche delle carte
    private static final int LARGHEZZA_CARTA = 120;
    private static final int ALTEZZA_CARTA = 180;
    private int indiceZOriginale; // utilizzato per l'effetto di sollevamento della carta (Balatro style)
    
    // bottoni dell'interfaccia
    private JButton btnPesca = new JButton();
    private JButton btnImpostazioni = new JButton();
    private JButton btnJUno = new JButton();
    private JButton btnContestaJUno = new JButton("<html><center>Contesta<br>JUno</center></html>");
    private JButton btnMostraCarte = new JButton("<html><center>Mostra<br>Carte</center></html>");
    private JButton btnGioca = new JButton("Gioca");
    
    // bottoni e pannelli per Popups
    private JPanel pannelloMano = new JPanel(new FlowLayout(FlowLayout.CENTER, -40, 0));
    private JButton buttonGiallo = new JButton("GIALLO");
    private JButton buttonRosso = new JButton("ROSSO");
    private JButton buttonVerde = new JButton("VERDE");
    private JButton buttonBlu = new JButton("BLU");
    private JDialog dialogColori; 
    
    // label informative
    private JLabel lblNumeroGiocatori = new JLabel("Giocatori :");
    private JLabel lblTurniPlayer = new JLabel("Turno di :");
    private JLabel lblProssimoTurno = new JLabel("Prossimo Giocatore :");
    private JLabel lblStaPensando = new JLabel("sta pensando...");
    private JLabel lblPilaScarti = new JLabel();
    
    // area di testo
    private JTextArea storicoLive = new JTextArea();
    
    // variabili di stato della View
    private Giocatore giocatorePrecedenteVisualizzato = null;
    private boolean carteScoperte = true;

    /**
     * Costruttore della classe PannelloTavolo.
     * Inizializza il layout principale, il colore di sfondo e divide 
     * graficamente il tavolo nelle zone Nord, Centro e Sud. 
     * Infine attiva i listener per le interazioni.
     * @param controller, il {@link GameController} che coordina la logica di gioco.
     */
    public PannelloTavolo(GameController controller) {
        this.controller = controller;
        setLayout(new BorderLayout()); 
        setBackground(FinestraPrincipale.VERDE);
        
        creaPannelloNord();
        creaPannelloCentro();
        creaPannelloSud();
        creaPannelloColori();
        attivaListener();
    }
    
    /**
     * Crea e configura il pannello superiore (Nord).
     * Contiene le informazioni sui giocatori totali, di chi è il turno attuale 
     * e chi sarà il prossimo a giocare.
     */
    private void creaPannelloNord() {
        // utilizzo il BorderLayout per distribuire al meglio le label
        JPanel pannelloNord = new JPanel(new BorderLayout());
        pannelloNord.setOpaque(false); // Sempre trasparente
        // aggiunta di margine per non attaccare le scritte ai bordi del monitor
        pannelloNord.setBorder(BorderFactory.createEmptyBorder(20, 30, 0, 30)); 
        //label giocatori SX
        lblNumeroGiocatori.setFont(FinestraPrincipale.FONT_TITOLI);
        lblNumeroGiocatori.setForeground(Color.WHITE);
        pannelloNord.add(lblNumeroGiocatori, BorderLayout.WEST);
        // label turno CENTRO
        lblTurniPlayer.setFont(FinestraPrincipale.FONT_TITOLI_30);
        lblTurniPlayer.setHorizontalAlignment(SwingConstants.CENTER); // Centra il testo esattamente in mezzo
        lblTurniPlayer.setForeground(Color.WHITE);  
        pannelloNord.add(lblTurniPlayer, BorderLayout.CENTER);
        // label prossimo giocatore DX
        lblProssimoTurno.setFont(FinestraPrincipale.FONT_TITOLI);
        lblProssimoTurno.setForeground(Color.WHITE);
        pannelloNord.add(lblProssimoTurno, BorderLayout.EAST);
        // aggancio tutto al pannelloTavolo inserendo a Nord
        add(pannelloNord, BorderLayout.NORTH);
    }
    /**
     * Crea e configura il pannello centrale.
     * Gestisce visivamente il mazzo da cui pescare, la pila degli scarti, 
     * il bottone per le impostazioni, il bottone per contestare JUno 
     * e lo storico in tempo reale delle mosse.
     */
    private void creaPannelloCentro() {
        GestoreAsset gestore = GestoreAsset.getInstance();
        java.awt.image.BufferedImage imgBack = gestore.getImmagine("back");
        java.awt.image.BufferedImage imgImpostazioni = gestore.getImmagine("impostazioni");
        
        // pannello principale che gestisce il layout centrale del tavolo
        JPanel pannelloCentro = new JPanel(new GridBagLayout());
        pannelloCentro.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); 
        
        // configurazione bottone impostazioni
        btnImpostazioni.setPreferredSize(new Dimension(80, 80));
        btnImpostazioni.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (imgImpostazioni != null) {
            java.awt.Image imgScalata = imgImpostazioni.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
            btnImpostazioni.setIcon(new ImageIcon(imgScalata));
            btnImpostazioni.setContentAreaFilled(false);
            btnImpostazioni.setBorderPainted(false);
            btnImpostazioni.setFocusPainted(false);
        } else {
            btnImpostazioni.setContentAreaFilled(true);
            btnImpostazioni.setBackground(Color.GRAY);
            btnImpostazioni.setText("Impostazioni");
        }
        
        // configurazione bottone pesca
        btnPesca.setPreferredSize(new Dimension(LARGHEZZA_CARTA, ALTEZZA_CARTA));
        btnPesca.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        if (imgBack != null) {
            java.awt.Image imgScalata = imgBack.getScaledInstance(LARGHEZZA_CARTA, ALTEZZA_CARTA, java.awt.Image.SCALE_SMOOTH);
            ImageIcon iconaMazzo = new ImageIcon(imgScalata);
            btnPesca.setIcon(iconaMazzo);
            btnPesca.setDisabledIcon(iconaMazzo);
            btnPesca.setContentAreaFilled(false);
            btnPesca.setBorderPainted(false);
            btnPesca.setFocusPainted(false);
        } else {
            btnPesca.setContentAreaFilled(true);
            btnPesca.setBackground(Color.RED);
            btnPesca.setText("PESCA");
        }
        
        // label pila scarti
        lblPilaScarti.setPreferredSize(new Dimension(LARGHEZZA_CARTA, ALTEZZA_CARTA));
        
        // bottone contesta
        btnContestaJUno.setPreferredSize(new Dimension(100, 80));
        
        // storico live
        storicoLive.setEditable(false);
        storicoLive.setFont(FinestraPrincipale.FONT_LABEL);
        storicoLive.setForeground(Color.WHITE);
        storicoLive.setOpaque(false);
        storicoLive.setLineWrap(true);
        storicoLive.setWrapStyleWord(true);
        JScrollPane scrollStorico = new JScrollPane(storicoLive);
        scrollStorico.setPreferredSize(new Dimension(250, 200)); 
        scrollStorico.setOpaque(false);
        scrollStorico.getViewport().setOpaque(false);
        scrollStorico.setBorder(BorderFactory.createEmptyBorder()); 
        scrollStorico.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        scrollStorico.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); // Nasconde lo scroll verticale
        
        // aggiunta dei componenti al pannello centrale
        pannelloCentro.add(btnImpostazioni, gbc);
        pannelloCentro.add(btnPesca, gbc);
        pannelloCentro.add(lblPilaScarti, gbc);
        pannelloCentro.add(btnContestaJUno, gbc);
        pannelloCentro.add(scrollStorico, gbc);
        
        add(pannelloCentro, BorderLayout.CENTER);
    }
    
    /**
     * Crea e configura il pannello inferiore (Sud).
     * Gestisce la visualizzazione della mano del giocatore corrente, calcolando 
     * lo spazio necessario in base al numero di carte possedute, e include 
     * i pulsanti di interazione diretta (JUno, Gioca e Mostra Carte).
     */
    private void creaPannelloSud() {
        JPanel pannelloSud = new JPanel(new GridBagLayout());
        pannelloSud.setOpaque(false); 
        pannelloSud.setBorder(BorderFactory.createEmptyBorder(50, 30, 20, 50)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); 
        
        pannelloMano.setOpaque(false);
        
        // label sta pensando
        lblStaPensando.setFont(FinestraPrincipale.FONT_TITOLI); 
        lblStaPensando.setHorizontalAlignment(SwingConstants.CENTER); 
        lblStaPensando.setForeground(Color.YELLOW); 
        lblStaPensando.setVisible(false); // Parte invisibile
        
        // creo un pannello raccoglitore trasparente
        JPanel wrapperMano = new JPanel(new BorderLayout());
        wrapperMano.setOpaque(false);
        wrapperMano.add(lblStaPensando, BorderLayout.NORTH); // mette la scritta in alto
        wrapperMano.add(pannelloMano, BorderLayout.CENTER);  // mette le carte subito sotto
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1; // prende tutto lo spazio orizzontale disponibile
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        
        // bottone mostra carte
        btnMostraCarte.setVisible(false);
        btnMostraCarte.setPreferredSize(new Dimension(100, 100));
        btnMostraCarte.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pannelloSud.add(btnMostraCarte);
        
        GestoreAsset gestore = GestoreAsset.getInstance();
        java.awt.image.BufferedImage imgJUNO = gestore.getImmagine("JUNO");
        
        pannelloSud.add(wrapperMano, gbc);
        
        //sottopannello per distanziare i vari bottoni a destra
        JPanel pannelloAzioniDestra = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        pannelloAzioniDestra.setOpaque(false);
        
        // bottone gioca (solo con Number Rush)
        btnGioca.setPreferredSize(new Dimension(80, 80));
        btnGioca.setVisible(true); // parte invisibile
        btnGioca.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pannelloAzioniDestra.add(btnGioca);
        
        // bottone JUno
        btnJUno.setPreferredSize(new Dimension(80, 80));
        btnJUno.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (imgJUNO != null) {
             java.awt.Image imgScalata = imgJUNO.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
             ImageIcon iconaJUno = new ImageIcon(imgScalata);
             btnJUno.setIcon(iconaJUno);
             btnJUno.setDisabledIcon(iconaJUno);
             btnJUno.setContentAreaFilled(false);
             btnJUno.setBorderPainted(false);
             btnJUno.setFocusPainted(false);
         } else {
             btnJUno.setContentAreaFilled(true);
             btnJUno.setText("JUNO");
         }
        pannelloAzioniDestra.add(btnJUno);        
        pannelloSud.add(pannelloAzioniDestra);
        add(pannelloSud, BorderLayout.SOUTH);
    }
    
    /**
     * Crea il pannello di popup (JDialog) contenente i quattro colori primari.
     * Esso si apre quando viene giocata una carta Jolly (WILD o WILD_FOUR)
     * e obbliga il giocatore a scegliere il nuovo colore del tavolo.
     */
    public void creaPannelloColori() {
    	//blu
        buttonBlu.setFont(FinestraPrincipale.FONT_TITOLI);
        buttonBlu.setBackground(Color.BLUE);
        buttonBlu.setForeground(Color.BLACK);
        //giallo
        buttonGiallo.setFont(FinestraPrincipale.FONT_TITOLI);
        buttonGiallo.setBackground(Color.YELLOW);
        buttonGiallo.setForeground(Color.BLACK);
        //verde
        buttonVerde.setFont(FinestraPrincipale.FONT_TITOLI);
        buttonVerde.setBackground(Color.GREEN);
        buttonVerde.setForeground(Color.BLACK);
        //rosso
        buttonRosso.setFont(FinestraPrincipale.FONT_TITOLI);
        buttonRosso.setBackground(Color.RED);
        buttonRosso.setForeground(Color.BLACK);
        JPanel pannelloColori = new JPanel(new GridLayout(2, 2)); // grid 2x2
        
        pannelloColori.add(buttonBlu);
        pannelloColori.add(buttonGiallo);
        pannelloColori.add(buttonVerde);
        pannelloColori.add(buttonRosso);
        JOptionPane optionPanel = new JOptionPane(
                pannelloColori,             
                JOptionPane.PLAIN_MESSAGE,  // messaggio normale
                JOptionPane.DEFAULT_OPTION, // opzioni di default
                null,                       // niente icone 
                new Object[]{}              // rimuove i pulsanti standard
            );
        dialogColori = optionPanel.createDialog(this, "Scegli un Colore");
        dialogColori.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // La X non può chiudere il popup
        dialogColori.setVisible(false); 
    }
    
    /**
     * Mostra un popup di conferma che chiede all'utente se desidera contestare 
     * un WILD_FOUR appena giocato dall'avversario precedente.
     * Delega la risposta al {@link GameController}.
     */
    public void mostraDecisioneChallenge() {
        int scelta = JOptionPane.showConfirmDialog(
                this,
                "Vuoi contestare il +4?",
                "Challenge",  
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE
        );
        controller.gestisciChallenge(scelta == JOptionPane.YES_OPTION); 
    }
    
    /**
     * Rende visibile il pannello popup per la scelta del colore.
     */
    public void mostraPannelloColori() {
        this.dialogColori.setLocationRelativeTo(null); // centra il popup ad ogni nuova apertura
        this.dialogColori.setVisible(true);
    }
    
    /**
     * Nasconde il pannello popup per la scelta del colore, una volta effettuata la selezione.
     */
    public void nascondiPannelloColori() {
        this.dialogColori.setVisible(false);
    }
    
    /**
     * Attiva i vari listener del pannello interfacciandoli con il Controller.
     */
    private void attivaListener() {
        buttonBlu.addActionListener(e -> {
            controller.cambioColore(Colori.BLU);
            nascondiPannelloColori(); 
        }); 
        buttonGiallo.addActionListener(e -> {
            controller.cambioColore(Colori.GIALLO);
            nascondiPannelloColori();
        }); 
        buttonVerde.addActionListener(e -> {
            controller.cambioColore(Colori.VERDE);
            nascondiPannelloColori();
        }); 
        buttonRosso.addActionListener(e -> {
            controller.cambioColore(Colori.ROSSO);
            nascondiPannelloColori();
        }); 
        btnPesca.addActionListener(e -> controller.azionePesca());
        btnJUno.addActionListener(e -> controller.gestisciJUno());
        btnContestaJUno.addActionListener(e -> controller.gestisciContestaJUno());
        btnGioca.addActionListener(e -> controller.confermaGiocata());
        btnImpostazioni.addActionListener(e -> controller.mostraImpostazioni(true));
    }
   
    /**
     * Metodo di supporto per convertire i valori e i colori di un oggetto {@link Carta} 
     * nella stringa chiave che il {@link GestoreAsset} si aspetta per il caricamento immagini.
     * @param carta, la carta da tradurre.
     * @return Stringa corrispondente alla chiave asset, o null se la carta è nulla.
     */
    private String traduciCartaInChiave(Carta carta) {
        if (carta == null) return null;
        return carta.getValore().name().toLowerCase() + "-" + carta.getColore().name().toLowerCase();
    }
   
    /**
     * Metodo principale di aggiornamento della View. Sincronizza l'interfaccia 
     * grafica con lo stato attuale della partita.
     * Aggiorna le etichette testuali, la visibilità e l'attivazione dei bottoni,
     * la pila degli scarti, la mano del giocatore corrente e lo storico mosse.
     * @param partita, oggetto {@link Partita} contenente lo stato di gioco aggiornato.
     */
    public void aggiorna(Partita partita) {
        String nomeCorrente = partita.getGiocatoreAttuale().getNome();
        String nomeSuccessivo = partita.getGiocatoreSuccessivo().getNome();
        int numGiocatori = partita.getNumGiocatori();
        Giocatore giocatoreAttuale = partita.getGiocatoreAttuale();
        
        // disattiva interazioni (eccetto impostazioni) se è il turno di un bot
        boolean toccaAdUnBot = giocatoreAttuale.isBot();
        btnPesca.setEnabled(!toccaAdUnBot);
        btnContestaJUno.setEnabled(!toccaAdUnBot);
        lblStaPensando.setVisible(toccaAdUnBot);
        
        // logica per nascondere le carte durante il cambio turno in multi-umano
        if (giocatorePrecedenteVisualizzato != giocatoreAttuale) {
            giocatorePrecedenteVisualizzato = giocatoreAttuale; 
            if (partita.getNumUmani() > 1 && !giocatoreAttuale.isBot()) {
                carteScoperte = false;
            } else {
                carteScoperte = true; 
            }
        }
        // reset del listener per mostrare le carte
        for (java.awt.event.ActionListener al : btnMostraCarte.getActionListeners()) {
            btnMostraCarte.removeActionListener(al);
        }
        btnMostraCarte.addActionListener(e -> {
            carteScoperte = true; // scopre le carte
            aggiornaManoGiocatore(partita);
        });
        // se la pila non è vuota, aggiorna la grafica della carta in cima
        if (partita.getTavolo().getPila().getSize() > 0) {
            Carta cima = partita.getTavolo().getPila().getCima();
            aggiornaPilaScarti(cima);
        }
        // aggiornamento delle etichette di testo
        if (lblTurniPlayer != null) {
            lblTurniPlayer.setText("Turno di: " + nomeCorrente);
        }
        if (lblProssimoTurno != null) {
            lblProssimoTurno.setText("Prossimo Giocatore: " + nomeSuccessivo);
        }
        if (lblNumeroGiocatori != null) {
            lblNumeroGiocatori.setText("Giocatori: " + numGiocatori);
        }
        //aggiorno la mano del giocatore e lo storico in base allo statoPartita corrente
        aggiornaManoGiocatore(partita);
        aggiornaStoricoLive(partita);
        repaint();
    }
    
    /**
     * Gestisce dinamicamente l'aggiornamento della mano del giocatore attuale.
     * Calcola dinamicamente le sovrapposizioni delle carte in caso di mani numerose,
     * copre le carte in modalità multi-umano finché non vengono svelate,
     * e applica gli effetti hover (Balatro-style) ed i listener per giocare la carta al click.
     * @param partita, oggetto {@link Partita} per ricavare la mano corrente e le regole.
     */
    private void aggiornaManoGiocatore(Partita partita) {
        Giocatore giocatoreCorrente = partita.getGiocatoreAttuale();
        ArrayList<Carta> mano = giocatoreCorrente.getMano();
        
        pannelloMano.removeAll(); 
        // gestione visibilità del bottone per mostrare le carte
        btnMostraCarte.setVisible(partita.getNumUmani() > 1 && !giocatoreCorrente.isBot() && !carteScoperte);
        // calcolo dinamico dello spazio tra le carte per evitare che sbordino dallo schermo
        int gap = -40;
        if (mano.size() > 1) {
            int larghezzaMassimaSchermo = this.getWidth() - 400;
            int gapProvvisorio = (larghezzaMassimaSchermo - LARGHEZZA_CARTA) / (mano.size() - 1) - LARGHEZZA_CARTA;
            gap = Math.min(-40, gapProvvisorio);
        }
        
        FlowLayout layout = (FlowLayout) pannelloMano.getLayout();
        layout.setHgap(gap);
        GestoreAsset gestore = GestoreAsset.getInstance();
        
        for (Carta cartaCorrente : mano) {
            JButton btnCarta = new JButton();
            // +30 per lasciare spazio all'effetto di sollevamento 
            btnCarta.setPreferredSize(new Dimension(LARGHEZZA_CARTA, ALTEZZA_CARTA + 30));
            btnCarta.setContentAreaFilled(false);
            btnCarta.setBorderPainted(false);
            btnCarta.setFocusPainted(false);
            java.awt.image.BufferedImage imgDaCaricare = null;
            boolean mostraDorso;
            if (partita.getNumUmani() == 0) {
                mostraDorso = false; 
            } else {
                mostraDorso = giocatoreCorrente.isBot() || (!carteScoperte && !giocatoreCorrente.isBot());
            }
            if (mostraDorso) {
                imgDaCaricare = gestore.getImmagine("back");
            } else {
                String chiave = traduciCartaInChiave(cartaCorrente);
                imgDaCaricare = gestore.getImmagine(chiave);
            }
            if (imgDaCaricare != null) {
                java.awt.Image imgScalata = imgDaCaricare.getScaledInstance(LARGHEZZA_CARTA, ALTEZZA_CARTA, java.awt.Image.SCALE_SMOOTH);
                btnCarta.setIcon(new ImageIcon(imgScalata));
            } else {
                btnCarta.setContentAreaFilled(true);
                btnCarta.setBackground(mostraDorso ? Color.GRAY : Color.WHITE);
                if (!mostraDorso) {
                    btnCarta.setText(traduciCartaInChiave(cartaCorrente));
                }
            }
            
            // bottone Gioca visibile solo se Number Rush è attivo e tocca a un Umano
            btnGioca.setVisible(partita.getRegole().getNumberRush() && !giocatoreCorrente.isBot());
            
            // interazioni attive solo se la carta è visibile (non di dorso)
            if (!mostraDorso && !giocatoreCorrente.isBot()) {
                btnCarta.setCursor(new Cursor(Cursor.HAND_CURSOR));
                boolean isSelezionata = controller.getCarteSelezionate().contains(cartaCorrente);
                // mantiene la carta sollevata se è stata selezionata
                if (isSelezionata) {
                    btnCarta.setVerticalAlignment(SwingConstants.TOP); 
                } else {
                    btnCarta.setVerticalAlignment(SwingConstants.BOTTOM); 
                }
                btnCarta.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        indiceZOriginale = pannelloMano.getComponentZOrder(btnCarta);
                        pannelloMano.setComponentZOrder(btnCarta, 0); // Porta in primo piano
                        if (!isSelezionata) {
                            btnCarta.setVerticalAlignment(SwingConstants.TOP);
                        }
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        pannelloMano.setComponentZOrder(btnCarta, indiceZOriginale); // Ripristina livello
                        if (!isSelezionata) {
                            btnCarta.setVerticalAlignment(SwingConstants.BOTTOM);
                        }
                    }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        controller.azioneGiocaCarta(cartaCorrente);
                    }
                });
            }
            pannelloMano.add(btnCarta);
        }
        pannelloMano.revalidate();
        pannelloMano.repaint();
    }
    /**
     * aggiorna graficamente l'immagine della carta attualmente in cima alla pila degli scarti.
     * @param cartaInCima, l'attuale carta in cima alla pila.
     */
     private void aggiornaPilaScarti(Carta cartaInCima) {
         if (cartaInCima == null) return;
         String chiaveCarta = traduciCartaInChiave(cartaInCima);
         GestoreAsset gestore = GestoreAsset.getInstance();
         java.awt.image.BufferedImage imgCarta = gestore.getImmagine(chiaveCarta);
         if (imgCarta != null) {
             java.awt.Image imgScalata = imgCarta.getScaledInstance(LARGHEZZA_CARTA, ALTEZZA_CARTA, java.awt.Image.SCALE_SMOOTH);
             lblPilaScarti.setIcon(new ImageIcon(imgScalata));
         } else {
             // fallback di sicurezza in caso manchi l'asset grafico
             lblPilaScarti.setOpaque(true);
             lblPilaScarti.setBackground(Color.WHITE);
             lblPilaScarti.setText(chiaveCarta);
         }
     }
        
     /**
      * Aggiorna lo storico testuale in sovrimpressione, concatenando 
      * le mosse effettuate ricavate dallo storico della partita.
      * @param partita, l'oggetto {@link Partita} da cui prendere lo storico mosse.
      */
     private void aggiornaStoricoLive(Partita partita) {
         ArrayList<String> tutte = partita.getCronologiaMosse();
         storicoLive.setText("");
         if (tutte.isEmpty()) {
             return; 
         }
         for (String mossa : tutte) {
             storicoLive.append("• " + mossa.trim() + "\n\n");
         }   
     }
        
     /**
      * Mostra un popup di avviso che informa il giocatore di aver tentato 
      * una mossa illegale rispetto alle regole di gioco.
      */
     public void mostraErroreMossaNonValida() {
         JOptionPane.showMessageDialog(
                 this,
                 "Mossa non valida!", 
                 "Attenzione",        
                 JOptionPane.WARNING_MESSAGE
         );
     }
}