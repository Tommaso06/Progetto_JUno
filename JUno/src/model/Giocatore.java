package model;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe astratta che definisce lo stato e i comportamenti di tutti i giocatori di JUno.
 * Gestisce elementi fondamentali come il nome, le carte in mano, il punteggio accumulato 
 * e lo stato delle meccaniche specifiche (Dichiarazione "JUNO" e Challenge del WILD_FOUR).
 */
public abstract class Giocatore implements Serializable{
	private String nomeGiocatore; 
	private ArrayList<Carta> manoGiocatore;
	private int puntiGiocatore; 
	private boolean dichiarazioneJuno = false; 
	private boolean challenge=false;
	
	
	/**
	 * Costruttore della classe astratta Giocatore.
	 * Assegna un nome al giocatore e ne inizializza le strutture dati per la mano e i punteggi.
	 * Se il nome fornito è nullo, imposta un nome di default di sicurezza.
	 * @param nomeGiocatore, il nome identificativo del giocatore.
	 */
	public Giocatore(String nomeGiocatore) {
	    if (nomeGiocatore == null){
	        this.nomeGiocatore = "Player Sconosciuto"; //controllo sicuro, anche se tutto viene gestito dalla view
	    } else { 
	    	this.nomeGiocatore= nomeGiocatore;
	    }
		this.manoGiocatore = new ArrayList<>();
		this.puntiGiocatore = 0;
	
	}
	
	/**
	 * Aggiunge i punti passati come parametro al totale accumulato dal giocatore.
	 * @param puntiDaAggiungere, i punti calcolati a fine round, da sommare al totale.
	 */
	public void aggiungiPunti(int puntiDaAggiungere) {
		this.puntiGiocatore = this.puntiGiocatore + puntiDaAggiungere;
	}
	
	/**
	 * Gestisce l'azione di pesca di una carta dal mazzo del tavolo.
	 * Se il mazzo è vuoto, delega al tavolo il rimescolamento della pila degli scarti 
	 * prima di effettuare la pesca.
	 */
	public void pesca(Tavolo tavolo) {
		if (tavolo.mazzo.isEmpty()) {
			ArrayList<Carta> carteDaRimescolare = tavolo.getPila().prelevaScarti();
			tavolo.mazzo.ricaricaScarti(carteDaRimescolare);
		}
		Carta cartaPescata = tavolo.mazzo.getCarta(); //pesca e la toglie dal mazzo
		this.manoGiocatore.add(cartaPescata);
	}
		
	/**
	 * Gioca la carta selezionata, rimuovendola dalla mano del giocatore 
	 * e aggiungendola alla pila degli scarti.
	 * @param carta, la {@link Carta} che il giocatore intende giocare.
	 * @param pila, la {@link PilaScarti} su cui posare la carta.
	 */
	public void giocaCarta(Carta carta, PilaScarti pila) {
		pila.aggiungiCarta(carta);
		this.manoGiocatore.remove(carta);
	}
	
	/** Imposta in modo sicuro se il giocatore subisce un WILD_FOUR. 
	 * @param stato, {@code true} se subisce il WILD_FOUR, 
	 * {@code false} per resettare lo stato.
	 */
	public void setChallenge(boolean stato) { this.challenge = stato; }
	
	/**Imposta in modo sicuro se il giocatore ha dichiarato JUNO. 
	 * @param stato, {@code true} se dichiara JUNO, 
	 * {@code false} altrimenti. 
	 */
	public void setDichiarazioneJuno(boolean stato) { this.dichiarazioneJuno = stato; }
	
	/** Restituisce il nome identificativo del giocatore. 
	 * @return nomeGiocatore. 
	 */
	public String getNome() { return this.nomeGiocatore; }	
	
	/** Restituisce la mano attuale del giocatore. 
	 * @return manoGiocatore. 
	 */
	public ArrayList<Carta> getMano() { return this.manoGiocatore; }
	
	/** Restituisce i punti totali accumulati dal giocatore. 
	 * @return puntiGiocatore. 
	 */
	public int getPunti() { return this.puntiGiocatore; }
	
	/** Calcola il punteggio totale della mano sommando i punti di ogni carta. 
	 * @return Il valore numerico totale. 
	 */
	public int getPuntiMano() { return this.manoGiocatore.stream().mapToInt(c -> c.getValore().getPunti()).sum(); }
	
	/** Indica se il giocatore ha subito un WILD_FOUR ed è in fase di Challenge.
	 *  @return {@code true} se ha subito un WILD_FOUR
	 *  {@code false} altrimenti. 
	 */
	public boolean getChallenge() { return this.challenge; }
	
	/** restituisce lo stato della dichiarazione JUNO del giocatore.
	 * @return {@code true} se lo ha dichiarato.
	 * {@code false} altrimenti. 
	 * */
	public boolean getDichiarazioneJuno() { return this.dichiarazioneJuno; }
	
	/** Verifica se l'istanza corrente è controllata da un bot o da un umano.
	 *  @return {@code true} se il giocatore è un bot,
	 *  {@code false} se è un umano. 
	 */
	public boolean isBot() { return this instanceof GiocatoreAI; }
	
	/** Restituisce la stringa rappresentativa del giocatore. 
	 * @return nomeGiocatore. 
	 * */
	@Override
	public String toString() { return this.nomeGiocatore; }
	
	
	
}
	

