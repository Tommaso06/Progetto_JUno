package model;

/**
 * Enum che definisce i valori delle carte del gioco JUno.
 * Ogni valore porta con se tre informazioni fondamentali per le regole e la struttura del gioco:
 * il valore numerico (usato per la logica del Number Rush e i check standard, -1 per le speciali),
 * il punteggio (usato a fine round per il calcolo dei punti per il vincitore),
 * e la quantità iniziale di queste carte presenti nel mazzo.
 */
public enum Valori {
	
	ZERO(0, 0, 1),
	UNO(1, 1, 2),
	DUE(2, 2, 2),
	TRE(3, 3, 2),
	QUATTRO(4, 4, 2),
	CINQUE(5, 5, 2),
	SEI(6, 6, 2),
	SETTE(7, 7, 2),
	OTTO(8, 8, 2),
	NOVE(9, 9, 2),
	SKIP(-1, 20, 2),
	REVERSE(-1, 20, 2), 
	DRAW_TWO(-1, 20, 2), 
	WILD(-1, 50, 4), 
	WILD_DRAW_FOUR(-1, 50, 4);  
	
	private final int valore;
	private final int punti;
	private final int quantita;
	
	/**
	 * Costruttore dell'enum.
	 * @param valore, il valore numerico della carta (-1 per indicare carte speciali senza numero).
	 * @param punti, il punteggio assegnato alla carta .
	 * @param quantita, il numero di copie di questa carta generate nel mazzo iniziale.
	 */
	Valori(int valore, int punti, int quantita) { 
		this.valore = valore;
		this.punti = punti;
		this.quantita = quantita;
	}

	// --- GETTER ---
	
	/** Restituisce il valore numerico della singola carta. 
	 * @return valore, il valore intero associato. 
	 */
	public int getValore() { return this.valore; }
	
	/** Restituisce il punteggio di penalità/vittoria della carta. 
	 * @return punti, i punti della carta. 
	 */
	public int getPunti() { return this.punti; }
	
	/** Restituisce il numero iniziale di queste carte da inserire nel mazzo. 
	 * @return quantita, la quantità. 
	 */
	public int getQuantita() { return this.quantita; }
}