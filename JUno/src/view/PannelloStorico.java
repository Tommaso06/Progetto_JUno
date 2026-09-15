package view;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Insets;
import controller.GameController;
import model.Partita;

/**
 * Rappresenta il pannello dedicato alla visualizzazione dello storico completo
 * delle mosse effettuate durante l'intero round o la partita.
 * Permette agli utenti di scorrere e rileggere tutte le azioni giocate, 
 * utile in caso di contestazioni o per rivedere la strategia.
 */
public class PannelloStorico extends JPanel {
    private GameController controller;
    private JTextArea areaTesto = new JTextArea(); 
    private JButton btnChiudi = new JButton("Chiudi");
    
    /**
     * Costruttore del {@link PannelloStorico}.
     * Inizializza l'interfaccia grafica e i componenti.
     * @param controller, il {@link GameController} per gestire la chiusura del pannello.
     */
    public PannelloStorico(GameController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());
        creaPannelloStorico();
        creaPannelloSud();
        attivaListener();
    }
    
    /**
     * Crea e configura il pannello centrale che contiene l'area di testo scrollabile.
     * L'area di testo viene resa non modificabile e configurata con un font leggibile.
     */
    private void creaPannelloStorico() {
        areaTesto.setEditable(false); // nessuno può scriverci dentro
        areaTesto.setFont(FinestraPrincipale.FONT_TITOLI);
        areaTesto.setLineWrap(true); // manda a capo in automatico se la finestra è piccola
        areaTesto.setWrapStyleWord(true); // non spezza le parole a metà
        areaTesto.setMargin(new Insets(20, 20, 20, 20)); // margine per dare respiro al testo
        
        JScrollPane scrollPane = new JScrollPane(areaTesto);
        this.add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Crea e configura il pannello inferiore (Sud).
     * Inserisce il bottone "Chiudi" all'interno di un pannello con FlowLayout 
     * in modo da mantenerne le dimensioni naturali e applica una spaziatura adeguata.
     */
    private void creaPannelloSud() {
        btnChiudi.setFont(FinestraPrincipale.FONT_NORMALE);
        JPanel pannelloSud = new JPanel();
        pannelloSud.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // 10px sopra e sotto
        pannelloSud.add(btnChiudi);
        this.add(pannelloSud, BorderLayout.SOUTH);
    }
    /**
     * Associa i listener ai bottoni per comunicare le azioni al controller.
     */
    private void attivaListener() {
        btnChiudi.addActionListener(e -> controller.chiudiStorico());
    }
    
    /**
     * Aggiorna il contenuto testuale dello storico estraendo le mosse 
     * dall'oggetto Partita. Pulisce l'area di testo e la ripopola formattando
     * ogni mossa, portando poi automaticamente la visualizzazione (auto-scroll) 
     * alla fine del documento.
     * @param partita, l'oggetto {@link Partita} da cui prelevare l'intera cronologia.
     */
    public void aggiorna(Partita partita) {
        areaTesto.setText("");
        for (String mossa : partita.getCronologiaMosse()) {
            areaTesto.append("• " + mossa.trim() + "\n\n");
        }
        
        // auto-scroll in fondo all'ultima mossa
        if (areaTesto.getDocument().getLength() > 0) {
            areaTesto.setCaretPosition(areaTesto.getDocument().getLength());
        }
    }
    
    // --- GETTER ---
    
    /** Restituisce l'area di testo contenente la cronologia. 
     * @return areaTesto. 
     */
    public JTextArea getAreaTesto() { return areaTesto; }
    
    /** Restituisce il bottone utilizzato per chiudere lo storico. 
     * @return btnChiudi. 
     */
    public JButton getBtnChiudi() { return btnChiudi; }
}