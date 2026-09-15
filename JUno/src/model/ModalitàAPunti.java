package model;

/**
 * Implementazione della modalità di gioco "A Punti".
 * In questa modalità, la partita è composta da più round. Un round termina quando 
 * un giocatore svuota la propria mano, permettendogli di accumulare i punti derivati 
 * dalle carte rimaste in mano agli avversari. La partita finale viene vinta dal primo 
 * giocatore che raggiunge o supera la soglia di punti prestabilita.
 */
public class ModalitàAPunti implements ModalitàGioco {
	private int puntiVittoria;

	/**
	 * Costruttore della Modalità a Punti.
	 * @param puntiVittoria, il punteggio target necessario per decretare il vincitore finale.
	 */
	public ModalitàAPunti(int puntiVittoria) {
		this.puntiVittoria = puntiVittoria;
	}
	
	/** Restituisce i punti necessari per vincere la partita. 
	 * @return puntiVittoria. 
	 */
	public int getPuntiVittoria() { return this.puntiVittoria; }
	
	/**
	 * Controlla se il giocatore ha raggiunto o superato i punti necessari per vincere la partita.
	 * @param giocatore, il {@link Giocatore} di cui verificare il punteggio.
	 * @return L'istanza del giocatore se ha vinto la partita, {@code null} altrimenti.
	 */
	@Override
	public Giocatore win(Giocatore giocatore) {
		return (giocatore.getPunti() >= this.puntiVittoria) ? giocatore : null;
	}
	
	/**
	 * Controlla se il giocatore ha vinto l'attuale round di gioco.
	 * La condizione di vittoria del round si verifica quando il giocatore rimane senza carte in mano.
	 * @param giocatore, il {@link Giocatore} di cui verificare la mano.
	 * @return L'istanza del giocatore se ha vinto il round, {@code null} altrimenti.
	 */
	@Override
	public Giocatore winRound(Giocatore giocatore) {
		return giocatore.getMano().isEmpty() ? giocatore : null;
	}
}