package model;

import java.io.Serializable;

/**
 * Rappresenta una singola carta del gioco JUno.
 * Ogni carta è immutabile e contiene le informazioni 
 * relative al suo colore e al suo valore numerico o speciale.
 */
public class Carta implements Serializable{
    
    private Colori colore;
    private Valori valore;

    /**
     * Costruisce una nuova Carta con il colore e il valore specificati.
     * @param colore, il colore della singola carta.
     * @param valore, il valore della singola carta.
     */
    public Carta(Colori colore, Valori valore) {
        this.colore = colore;
        this.valore = valore;
    }
    
    /**
     * Controlla se la carta inserita come parametro è un numero (per facilitare i check)
     */
    public boolean isNumero() {
    	return (!(this.valore==Valori.SKIP || this.valore == Valori.REVERSE || 
	            this.valore == Valori.DRAW_TWO || this.valore == Valori.WILD || 
	            this.valore == Valori.WILD_DRAW_FOUR));
    }
    
    /**
     * ritorna per ogni singola carta il proprio colore
     * @return colore.
     */
    public Colori getColore() {return this.colore;}
    
    /**
     * ritorna per ogni singola carta il proprio valore
     * @return valore.
     */
    public Valori getValore() {return this.valore;}
    
    /**
     * Restituisce una rappresentazione testuale della carta.
     * 
     * @return una stringa contenente il colore e il valore della carta
     */
    public String toString() {return ""+colore +" "+ valore;}
}


