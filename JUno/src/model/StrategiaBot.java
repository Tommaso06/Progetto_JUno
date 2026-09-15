package model;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Interfaccia che definisce la struttura del ragionamento dei vari bot e dei loro comportamenti.
 * Utilizza il pattern architetturale Strategy per permettere al gioco di cambiare 
 * dinamicamente le condizioni di vittoria senza modificare il motore centrale.
 * Ogni modalità di gioco specifica deve implementare le proprie logiche per 
 * la fine del round e la fine della partita
 */
public interface StrategiaBot extends Serializable { //Da finire
	
	/**
	 * Il metodo che restituirà un colore, in caso di Jolly, in base alla difficoltà del bot scelta dal giocatore.
	 * @param manoBot le carte in mano al bot al momento della scelta del colore
	 * @return un colore in base al livello del bot
	 */
	Colori scegliColore(ArrayList<Carta> manoBot);
	
	/**
	 * Metodo cardine del turno dei vari Bot che seleziona le carte da giocare in base alle strategie del bot.
	 * @param manoBot le carte in mano ad inizio turno
	 * @param statoPartita lo stato della partita ad inizio turno
	 * @return le carte selezionate da giocare, {@code null} se non ci sono carte valide
	 */
	ArrayList<Carta> scegliCarta(ArrayList<Carta> manoBot, Partita statoPartita);
	
	/**
	 * Metodo che serve per la contestazione del challenge, in caso di +4 giocato, in base a determinate condizioni date
	 * dai parametri del metodo.
	 * @param manoBot la mano del bot al mpmento della challenge
	 * @param statoPartita lo stato della partita al momento della challenge
	 * @return true in base alle condizioni dettate dal grado strategico del bot, false altrimenti.
	 */
	boolean decisioneChallenge(ArrayList<Carta> manoBot, Partita statoPartita);
	
	/**
	 * Definisce la dichiarazione di JUno, uno stato fondamentale del gioco, per i bot in base alla loro strategia prevista.
	 * @return true in base alle scelte del bot, false altrimenti
	 */
	boolean dichiarazioneJUno();
	
	/**
	 * Metodo che serve al bot per capire se è opportuno dubitare della mancanza dichiarazione di JUno da parte di uno
	 * degli altri giocatori.
	 * @param statoPartita lo stato della partita in quel momento
	 * @return true se la strategia del bot lo ritiene opportuno, false altrimenti.
	 */
	boolean contestazioneJUno(Partita statoPartita);
	

}
