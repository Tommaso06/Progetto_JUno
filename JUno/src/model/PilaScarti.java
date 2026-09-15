package model;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Rappresenta la pila degli scarti sul tavolo da gioco.
 * Utilizza una struttura dati LIFO (Last In, First Out) tramite {@link ArrayDeque}
 * per garantire prestazioni ottimali in lettura e scrittura della carta in cima.
 */
public class PilaScarti implements Serializable {

    private ArrayDeque<Carta> Pila;
    
    /**
     * Costruttore della pila degli scarti.
     * Inizializza la struttura dati impostando una capacità di base 
     * pari al numero totale di carte presenti nel gioco.
     */
    public PilaScarti() {
        this.Pila = new ArrayDeque<>(Mazzo.CARTE_TOTALI);
    }
    
    /** Aggiunge la carta specificata in cima alla pila degli scarti. 
     * @param carta, la carta giocata. 
     */
    public void aggiungiCarta(Carta carta) { this.Pila.push(carta); }
    
    /** Mostra la carta attualmente in cima alla pila senza rimuoverla. 
     * @return la carta visibile sul tavolo. 
     */
    public Carta getCima() { return this.Pila.peek(); }
    
    /** Metodo rapido per ottenere il colore della carta in cima. 
     * @return Il colore attuale. 
     */
    public Colori getColore() { return getCima().getColore(); }
    
    /** Metodo rapido per ottenere il valore della carta in cima. 
     * @return Il valore attuale. 
     */
    public Valori getValore() { return getCima().getValore(); }
    
    /** Restituisce il numero totale di carte scartate. 
     * @return La grandezza della pila. 
     */
    public int getSize() { return this.Pila.size(); }
    
    /** Estrae la carta in cima alla pila, rimuovendola. 
     * @return La carta rimossa. 
     */
    public Carta scartoCima() { return this.Pila.pop(); }

    /**
     * Svuota l'intera pila degli scarti ad eccezione della carta attualmente in cima.
     * Le carte prelevate vengono inserite in una lista pronta per essere
     * passata al mazzo principale quando quest'ultimo si esaurisce.
     * @return Un {@link ArrayList} contenente tutte le carte sottostanti alla cima, pronte da rimescolare.
     */
    public ArrayList<Carta> prelevaScarti() {
    	
        Carta cima = this.Pila.pop(); // toglie e salva temporaneamente la carta in cima
        ArrayList<Carta> carteDaRimescolare = new ArrayList<>(this.Pila);
        this.Pila.clear(); // svuota completamente la pila originale
        this.Pila.push(cima); // riposiziona la cima salvata all'inizio
        return carteDaRimescolare; 
    }
    
    /** Restituisce una rappresentazione testuale della grandezza della pila e della carta in cima. 
     * @return Stringa descrittiva. 
     */
    @Override
    public String toString() { 
        return "Dimensione Pila: " + this.Pila.size() + "\nCima: " + this.Pila.peek().toString(); 
    }
}