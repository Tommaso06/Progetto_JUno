package model;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe dedicata alla registrazione cronologica degli eventi della partita.
 * Separa la logica di logging dalle meccaniche di gioco del Model,
 * permettendo alla View di recuperare la lista delle azioni effettuate.
 */
public class StoricoPartita implements Serializable{
    private ArrayList<String> mosse;
    /**
     * Costruttore che inizializza una nuova istanza dello storico vuoto.
     */
    public StoricoPartita() {
        this.mosse = new ArrayList<>();
    }

    /** Aggiunge una nuova mossa o evento in coda allo storico. 
     * @param mossa, la stringa descrittiva dell'evento. 
     */
    public void aggiungiMossa(String mossa) { this.mosse.add(mossa); }

    /** Restituisce l'intera cronologia delle mosse. 
     * @return mosse, una lista di stringhe rappresentante gli eventi. 
     */
    public ArrayList<String> getMosse() { return this.mosse; }
    
    /** Svuota completamente lo storico (chiamato all'inizio di un nuovo round o di una nuova partita). 
    */
    public void pulisciStorico() { this.mosse.clear(); }
}