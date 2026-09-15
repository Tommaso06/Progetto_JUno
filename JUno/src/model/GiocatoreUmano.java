package model;

/**
 * Rappresenta un giocatore umano. Estende la classe astratta {@link Giocatore} 
 * ereditandone le caratteristiche di base. A differenza del {@link GiocatoreAI}
 * le mosse e le decisioni di questa classe non sono automatizzate, 
 * ma vengono attivate interamente dagli input dell'utente tramite l'interfaccia grafica.
 */
public class GiocatoreUmano extends Giocatore {
	
    /**
     * Costruisce una nuova istanza di un giocatore umano.
     * Richiama il costruttore della superclasse per l'assegnazione del nome 
     * e l'inizializzazione delle strutture dati.
     * @param nome, il nome identificativo scelto dall'utente inserito in fase di configurazione.
     */
	public GiocatoreUmano(String nome) {
		super(nome);
	}

}