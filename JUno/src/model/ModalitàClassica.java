package model;

/**
 * Implementazione della modalità di gioco "Classica".
 * In questa modalità non c'è accumulo di punti: il gioco è composto da un 
 * singolo round. Il primo giocatore che riesce a svuotare completamente 
 * la propria mano vince istantaneamente l'intera partita.
 */
public class ModalitàClassica implements ModalitàGioco {
	
	/**
	 * Controlla se il giocatore ha vinto la partita svuotando la propria mano.
	 * @param giocatore, il {@link Giocatore} di cui verificare la vittoria.
	 * @return L'istanza del giocatore se ha vinto la partita, {@code null} altrimenti.
	 */
	@Override
	public Giocatore win(Giocatore giocatore) {
		return giocatore.getMano().isEmpty() ? giocatore : null;
	}
	
	/**
	 * Controlla se il giocatore ha vinto l'attuale round di gioco.
	 * Poiché nella modalità classica la vittoria del round coincide esattamente 
	 * con la vittoria della partita, questo metodo delega il controllo a {@link #win(Giocatore)}.
	 * @param giocatore, il {@link Giocatore} di cui verificare le carte.
	 * @return L'istanza del giocatore se ha vinto il round, {@code null} altrimenti.
	 */
	@Override
	public Giocatore winRound(Giocatore giocatore) {
		return win(giocatore);
	}
}