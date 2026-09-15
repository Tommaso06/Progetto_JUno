package model;

/**
 * Rappresenta un giocatore Bot. Estende la classe astratta {@link Giocatore} 
 * ereditandone le caratteristiche di base e, funge da struttura 
 * per implementare la logica decisionale automatizzata durante la partita.
 */
public class GiocatoreAI extends Giocatore {
	
	private StrategiaBot strategia;

    /**
     * Costruisce una nuova istanza di un giocatore controllato dal computer.
     * Richiama il costruttore della superclasse per l'assegnazione del nome 
     * e l'inizializzazione delle strutture dati.
     * @param nome, il nome identificativo assegnato al bot.
     */
    public GiocatoreAI(String nome, StrategiaBot strategia) {
        super(nome);
        this.strategia = strategia;
    }
    /*
     * Metodo Getter per ottenere la difficoltà del bot di turno
     */
    public StrategiaBot getStrategia() {
    	return this.strategia;
    }
}