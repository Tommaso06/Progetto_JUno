package model;

import java.io.Serializable;

/**
 * Rappresenta il tavolo da gioco centrale di JUno.
 * Contiene e gestisce i due elementi fisici fondamentali per lo scorrimento
 * della partita: il mazzo da cui i giocatori pescano e la pila degli scarti.
 */
public class Tavolo implements Serializable{
	private PilaScarti pila;
	protected Mazzo mazzo;
	
	/**
	 * Costruttore della classe Tavolo.
	 * Inizializza il mazzo e la pila degli scarti, mescolando le carte 
	 * e assicurandosi che la prima carta scoperta sul tavolo 
	 * all'inizio della partita non sia un Jolly (regola standard di JUNO).
	 */
	public Tavolo() {
		this.mazzo = new Mazzo();
		this.pila = new PilaScarti();
		
		// Garantisce che la prima carta scoperta non sia un Jolly.
		// In caso contrario, rimescola finché non esce una carta base.
		while (mazzo.guardaInCima().getColore() == Colori.JOLLY) { 
			mazzo.Shuffle(); 
		}
		
		// pesca la prima carta valida dal mazzo e la posiziona sulla pila
		Carta primaCarta = mazzo.getCarta();
		this.pila.aggiungiCarta(primaCarta);
	}

	// --- GETTER e SETTER ---
	
	/** Sostituisce la pila degli scarti. 
	 * @param pila, la nuova PilaScarti da impostare. 
	 */
	public void setPila(PilaScarti pila) { this.pila = pila; }
	
	/** Restituisce la pila degli scarti attuale. 
	 * @return pila. 
	 */
	public PilaScarti getPila() { return this.pila; }

	/** Restituisce il mazzo di carte principale. 
	 * @return mazzo. 
	 */
	public Mazzo getMazzo() { return this.mazzo; }
	
	/**
	 * Restituisce una rappresentazione testuale dello stato del tavolo.
	 * @return Stringa descrittiva contenente le info di mazzo e pila.
	 */
	@Override
	public String toString() {
		return "Mazzo: " + mazzo.toString() + "\nPila Scarti: " + pila.toString();
	}
	
}