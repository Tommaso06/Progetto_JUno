package model;

import java.io.Serializable;

/**
 * Interfaccia che definisce il contratto per le diverse regole di vittoria di JUno.
 * Utilizza il pattern architetturale Strategy per permettere al gioco di cambiare 
 * dinamicamente le condizioni di vittoria senza modificare il motore centrale.
 * Ogni modalità di gioco specifica deve implementare le proprie logiche per 
 * la fine del round e la fine della partita.
 */
public interface ModalitàGioco extends Serializable{
	
	/**
	 * Verifica se il giocatore ha soddisfatto le condizioni assolute per vincere 
	 * l'intera partita (es. raggiungere un tot di punti o svuotare la mano).
	 * @param giocatore, il {@link Giocatore} di cui verificare lo stato.
	 * @return L'istanza del giocatore se è il vincitore definitivo, {@code null} altrimenti.
	 */
	Giocatore win(Giocatore giocatore);
	
	/**
	 * Verifica se il giocatore ha soddisfatto le condizioni per vincere il round corrente.
	 * @param giocatore, il {@link Giocatore} di cui verificare lo stato.
	 * @return L'istanza del giocatore se ha vinto il round, {@code null} altrimenti.
	 */
	Giocatore winRound(Giocatore giocatore);
}