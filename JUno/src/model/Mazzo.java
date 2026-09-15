package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Rappresenta il mazzo principale di carte di JUno.
 * Gestisce la creazione delle 108 carte standard, il rimescolamento 
 * e le operazioni di pesca durante la partita.
 */
public class Mazzo implements Serializable{
	
    public static final int CARTE_TOTALI = 108; 
    protected ArrayList<Carta> Deck;
    
    /**
     * Costruttore della classe Mazzo.
     * Inizializza la struttura dati, genera le 108 carte standard
     * e le mescola, rendendo il mazzo pronto per l'inizio della partita.
     */
    public Mazzo() {
        this.Deck = new ArrayList<>(CARTE_TOTALI); 
        initCarte();
        Shuffle();
    }
    /**
     * Genera sistematicamente tutte le 108 carte del gioco basandosi sulle regole standard.
     * Inserisce le carte colorate e infine i Jolly.
     */
    private void initCarte() {
        // itero su tutti i colori tranne il colore JOLLY
        for(Colori colore : Colori.values()) {
            if (colore == Colori.JOLLY){
                continue; 
            }
            // itero su tutti i valori tranne i valori speciali WILD e WILD_DRAW_FOUR
            for (Valori valore : Valori.values()) {
                if (valore == Valori.WILD || valore == Valori.WILD_DRAW_FOUR) {
                    continue;
                }
                // per ogni carta aggiungo al deck la sua quantità iniziale definita nell'enum
                for (int i = 0; i < valore.getQuantita(); i++) {
                    Deck.add(new Carta(colore, valore));
                }
            }
        }
        // aggiungo i 4 Wild 
        for (int i = 0; i < Valori.WILD.getQuantita(); i++) {
            Deck.add(new Carta(Colori.JOLLY, Valori.WILD));
        }
        // aggiungo i 4 Wild Draw Four
        for (int i = 0; i < Valori.WILD_DRAW_FOUR.getQuantita(); i++) {
            Deck.add(new Carta(Colori.JOLLY, Valori.WILD_DRAW_FOUR));
        }
    }

    /** mescola casualmente l'ordine delle carte nel mazzo utilizzando {@link Collections#shuffle}. */
    public void Shuffle() { Collections.shuffle(Deck); }
    
    /** guarda e restituisce la carta in cima al mazzo senza rimuoverla. 
     * @return La carta in cima (indice 0). 
     */
    public Carta guardaInCima() { return this.Deck.get(0); }
    
    /** Restituisce il numero di carte attualmente presenti nel mazzo. 
     * @return La dimensione del mazzo. 
     */
    public int getSize() { return Deck.size(); }
    
    /** Rimuove la carta in cima al mazzo (indice 0) e la restituisce al chiamante.
     * 	@return La carta pescata. 
     */
    public Carta getCarta() { return Deck.remove(0); }
    
    /** Verifica se il mazzo è rimasto senza carte.
     *  @return true se il mazzo è vuoto, false altrimenti. 
     */
    public Boolean isEmpty() { return this.Deck.isEmpty(); }
    
    /**
     * Ricarica il mazzo utilizzando le carte provenienti dalla pila degli scarti, 
     * accodandole e rimescolando il tutto.
     * @param cartaDaRimescolare, la lista delle carte scartate da reinserire nel mazzo.
     */
    public void ricaricaScarti(ArrayList<Carta> cartaDaRimescolare) {
        this.Deck.addAll(cartaDaRimescolare); // uso addAll per evitare che l'istanza del deck venga sostituita
        Shuffle();
    }
    
    /**
     * Restituisce una rappresentazione testuale del mazzo, stampando tutte le carte 
     * contenute al suo interno e la loro quantità totale.
     * @return La stringa formattata con l'elenco delle carte e la grandezza del mazzo.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < Deck.size(); i++) {
            s.append(Deck.get(i)).append("\n");
        }
        return "Mazzo:\n" + s.toString() + "Totale carte: " + getSize();
    }
}