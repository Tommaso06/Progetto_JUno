package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Gestisce il motore delle regole del gioco JUno.
 * Si occupa della validazione delle mosse, dell'applicazione degli effetti speciali 
 * delle carte e della gestione delle varianti, opzionali (Stacking, Number Rush).
 */
public class Regole implements Serializable{
	
	private boolean Stacking, NumberRush, SoloBot;
	private Colori coloreChallenge = null; 
	private Colori coloreProvvisorio = null; 
	private Giocatore ultimoAccusato = null;
	/**
	 * Costruttore della classe Regole.
	 * @param Stacking, attiva la regola di accumulo delle penalità.
	 * @param NumberRush, attiva la regola di giocata multipla di carte con stesso valore (solo carte numeriche).
	 * @param SoloBot, flag che indica se la partita è giocata esclusivamente dai bot.
	 */
	public Regole(boolean Stacking, boolean NumberRush, boolean SoloBot) {
		this.Stacking = Stacking;
		this.NumberRush = NumberRush;
		this.SoloBot = SoloBot;
	}
	/**
	 * Controlla se il colore della carta giocata combacia con quello in cima alla pila,
	 * oppure se la carta giocata è un Jolly, o se corrisponde al colore provvisorio. 
	 * @param cartaGiocata, la carta che si intende giocare.
	 * @param cartaPila, la carta attualmente in cima alla pila degli scarti.
	 * @return true, se i colori sono compatibili, false altrimenti.
	 */
	private boolean checkColore(Carta cartaGiocata, Carta cartaPila) {
		return cartaGiocata.getColore() == cartaPila.getColore() || 
	           cartaGiocata.getColore() == Colori.JOLLY || 
	           cartaGiocata.getColore() == this.coloreProvvisorio;
	}
	/**
	 * Controlla se il valore della carta giocata combacia con quello in cima alla pila.
	 * @param cartaGiocata, la carta che si intende giocare.
	 * @param cartaPila, la carta attualmente in cima alla pila degli scarti.
	 * @return true se i valori combaciano, false altrimenti.
	 */
	private boolean checkValore(Carta cartaGiocata, Carta cartaPila) {
		return cartaGiocata.getValore().equals(cartaPila.getValore());
	}
	/**
	 * Controlla la giocabilità dell'intera mano del giocatore (per un eventuale challenge). 
	 * @param giocatore, il giocatore da controllare.
	 * @param cartaPila, la cima della pila da usare per il check.
	 * @return true se il giocatore ha almeno una carta valida da giocare, false altrimenti.
	 */
	public boolean checkMano(Giocatore giocatore, Carta cartaPila) { 
		for (Carta carta: giocatore.getMano()) {
			if (this.mossaValida(carta, cartaPila)) {
				return true;
			}
		}
		return false;
	}	
	
	/**
	 * Controlla se il giocatore ha effettuato la dichiarazione JUNO pur avendo una sola carta in mano.
	 * @param giocatore, il giocatore da verificare.
	 * @return true, se il giocatore ha esattamente una carta e ha dichiarato JUNO, false altrimenti.
	 */
	public boolean checkUno(Giocatore giocatore) {
		return giocatore.getMano().size() == 1 && giocatore.getDichiarazioneJuno();
	}

	/**
	 * Applica immediatamente alla partita gli effetti speciali derivanti dalla carta giocata.
	 * @param cartaGiocata, la carta speciale appena giocata.
	 * @param partita, L'istanza della partita in corso.
	 */
	public void applicaCarta(Carta cartaGiocata, Partita partita) {
		switch(cartaGiocata.getValore()) {
			case SKIP :
				partita.skipTurno();
				break;
				
			case REVERSE : 
				// se la partita è a 2 giocatori, il Reverse funge da Skip
				if (partita.getNumGiocatori() == 2) {
					partita.skipTurno();
				} else {
					partita.reverseTurno();
				}	
				break;
				
			case DRAW_TWO :
				// se c'è lo stacking aggiungo la penalità all'accumulatore
				if (Stacking) { 
					partita.aggiungiPenalita(2); 
				} else {
					// altrimenti il prossimo pesca subito 2 carte e salta il turno
					Giocatore successivo = partita.getGiocatoreSuccessivo();
					successivo.pesca(partita.getTavolo());
					successivo.pesca(partita.getTavolo());
					partita.skipTurno(); 
					partita.getStorico().aggiungiMossa(successivo.getNome() + " ha pescato 2 carte");
				}
				break;

			case WILD_DRAW_FOUR : 
				// se c'è lo stacking aggiungo solo la penalità
				if (Stacking) {
					partita.aggiungiPenalita(4);
				} else {
					// se il WILD_FOUR è l'ultima carta, non si può contestare
					if (partita.getGiocatoreAttuale().getMano().size() != 0) {
						partita.getGiocatoreSuccessivo().setChallenge(true);
						this.setAccusato(partita.getGiocatoreAttuale()); 
					}
				}
				break; 
			case WILD : 
				// gestita tramite cambioColore() all'interno della Partita
				break; 
				
			default : 
				// le carte numeriche (0-9) non producono effetti speciali
				break;
		}
	}
	
	/**
	 * Gestisce la regola del Challenge quando un giocatore gioca un WILD_FOUR.
	 * Verifica se l'accusato possedeva in mano una carta del colore precedente alla giocata.
	 * @param giocatoreAccusato, il giocatore che ha giocato il WILD_FOUR.
	 * @return true, se il giocatore ha barato, false altrimenti.
	 */
	public boolean challenge(Giocatore giocatoreAccusato) {
		if (this.coloreChallenge == null) return false; 
		for (Carta carta : giocatoreAccusato.getMano()) {	
			if (carta.getColore() == this.coloreChallenge) return true;
		}
		return false; 
	}
	
	/**
	 * Controlla se la singola mossa effettuata è valida.
	 * @param cartaGiocata, la carta scelta dal giocatore.
	 * @param cartaPila, la carta in cima agli scarti.
	 * @return true, se la mossa è valida, false altrimenti.
	 */
	public boolean mossaValida(Carta cartaGiocata, Carta cartaPila) {
		return this.checkValore(cartaGiocata, cartaPila) || this.checkColore(cartaGiocata, cartaPila);
	}
	
	/**
	 * Controlla se l'intero gruppo di carte selezionato può essere giocato in simultanea.
	 * Gestisce la regola alternativa "Number Rush" (giocata multipla di carte con stesso numero).
	 * @param carteGiocate, lista di carte che il giocatore vuole calare insieme.
	 * @param cartaPila, la carta attualmente in cima alla pila degli scarti.
	 * @return true, se il Number Rush è lecito, false altrimenti.
	 */
	public boolean checkNumberRush(ArrayList<Carta> carteGiocate, Carta cartaPila) {
        if (carteGiocate == null || carteGiocate.isEmpty()) {
            return false; 
        }
        Valori primoValore = carteGiocate.get(0).getValore();
        // ritorno false per le carte speciali 
        if (primoValore == Valori.SKIP || primoValore == Valori.REVERSE || 
            primoValore == Valori.DRAW_TWO || primoValore == Valori.WILD || 
            primoValore == Valori.WILD_DRAW_FOUR) {
            return false; 
        }
        // verifico che tutte le carte selezionate abbiano lo stesso valore
        for (Carta carta : carteGiocate) {
            if (!carta.getValore().equals(primoValore)) {
                return false; 
            }
        }
        return this.mossaValida(carteGiocate.get(0), cartaPila);
    }
	
	/**
	 * Controlla se la carta giocata è valida per difendersi dallo stacking in corso.
	 * (si risponde a un WILD_TWO solo con un WILD_TWO, o a un WILD_FOUR con un WILD_FOUR).
	 */
	public boolean checkGiocataStacking(Carta cartaGiocata, Carta cartaPila) {
		boolean èCartaPesca = cartaGiocata.getValore() == Valori.DRAW_TWO 
                || cartaGiocata.getValore() == Valori.WILD_DRAW_FOUR;
		return èCartaPesca && cartaGiocata.getValore() == cartaPila.getValore();
	}
	
	/**
	 * Controlla se il giocatore possiede in mano almeno una carta utile 
	 * per rispondere allo stacking attuale.
	 */
	public boolean checkStackingCarte(Giocatore giocatore, Carta cartaPila) {
		for (Carta carta : giocatore.getMano()) {
			if (this.checkGiocataStacking(carta, cartaPila)) {
				return true;
			}
		}
		return false;
	}
	
	// --- GETTER & SETTER ---
	
	/** Imposta un nuovo colore provvisorio sul tavolo da gioco. 
	 * @param colore, il colore scelto. 
	 */
	public void setColoreProvvisorio(Colori colore) { this.coloreProvvisorio = colore; }
	
	/** Imposta il giocatore accusato di aver giocato illegalmente un WILD_FOUR. 
	 * @param giocatore, il giocatore accusato. 
	 */
	public void setAccusato(Giocatore giocatore) { this.ultimoAccusato = giocatore; }
	
	/** Imposta il colore di riferimento da utilizzare per verificare il challenge. 
	 * @param colore, il colore salvato. 
	 */
	public void setColoreChallenge(Colori colore) { this.coloreChallenge = colore; }
    
	/** Restituisce l'ultimo giocatore accusato in merito alla verifica di un WILD_FOUR. 
	 * @return ultimoAccusato, il giocatore accusato. 
	 */
	public Giocatore getAccusato() { return this.ultimoAccusato; }
	
	/** Restituisce il colore provvisorio attualmente attivo sul tavolo. 
	 * @return Il colore, o null. 
	 */
	public Colori getColoreProvvisorio() { return this.coloreProvvisorio; }
	
	/** Verifica se la regola alternativa "Number Rush" è attiva. 
	 * @return true se è attiva, false altrimenti. 
	 */
	public boolean getNumberRush() { return this.NumberRush; }
	
	/** Verifica se la regola alternativa "Stacking" è attiva. 
	 * @return true se è attiva, false altrimenti. 
	 */
	public boolean getStacking() { return this.Stacking; }
}