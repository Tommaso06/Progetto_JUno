package model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Rappresenta la struttura del BotAggressivo, gestisce le scelte in base a parametri definiti dall'interfaccia 
 * StrategiaBot.
 */
public class BotAggressivo implements StrategiaBot {
	//Creo il campo statico che si occuperà del RNG del bot
	private static Random random = new Random();

	
	/**
	 * Il metodo restituisce un colore col 25% di possibilità per ognuno
	 * @param manoBot, la mano del bot in quel momento
	 * @return scelte, il colore estratto casualmente
	 */
	public Colori scegliColore(ArrayList<Carta> manoBot) {
		Colori[] scelte= {Colori.ROSSO, Colori.BLU, Colori.VERDE, Colori.GIALLO};
		return scelte[random.nextInt(scelte.length)];
	}
	
	/**Il metodo si occupa della decisione o meno di contestare il +4 giocato dal giocatore precedente.
	 * @param manoBot le carte in mano al bot per decidere in base al loro numero
	 * @param statoPartita per accedere al numero di carte in mano al giocatore precedente
	 * @return true se una delle condizioni è verà o col 25% di chance, false altrimenti
	 */
	public boolean decisioneChallenge(ArrayList<Carta> manoBot, Partita statoPartita) {
		int valore = random.nextInt(100);
		if( manoBot.size()>=7 || manoBot.size()<=2 || valore<25) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * Metodo che si occupa della contestazione del JUno da parte degli altri player. Appena un giocatore non dichiarera Juno
	 * il bot chiamerà la contestazione
	 * @param statoPartita,per controllare i giocatori avversari
	 * @return true, se un giocatore ha una carta ma non ha dichiarato JUno, false altrimenti.
	 */
	public boolean contestazioneJUno(Partita statoPartita) {
		for (Giocatore giocatore: statoPartita.getGiocatori()) {
			if(giocatore.equals(statoPartita.getGiocatoreAttuale())) continue;
			if(giocatore.getMano().size()==1 && !giocatore.getDichiarazioneJuno()) {
				return true;
			}
		}
		return false;
	}
	
	
	
	/**Il bot dichiara JUno con un 85% di possibilità
	 * @return Se la condizione viene soddisfatta ritornerà true, false altrimenti	 
	 */
	public boolean dichiarazioneJUno() {
		int valore= random.nextInt(100);
		if(valore<85) return true;
		return false;
	}

	/**
	 * Metodo che si occupa di decidere le carte che il bot dovrà giocare. Essendo il Bot Aggressivo esso giocherà
	 * una carta speciale se ne ha una, dopo aver effettuato la risposta in caso di Stacking. Dopodichè verrà effettuato
	 * NRush in caso esso sia attivo e solo alla fine verrà giocata la prima carta numerica valida
	 * @param manoBot, le carte in mano al bot durante il suo turno
	 * @param statoPartita, lo stato della partita in quel momento
	 * @return carteGiocate, le carte da giocare dopo i vari controlli
	 */
	public ArrayList<Carta> scegliCarta(ArrayList<Carta> manoBot, Partita statoPartita) {
		//Memorizzo la cimaPila per semplificare il codice e creo un ArrayList delle carte che il bot andrà a giocare
		Carta cimaPila= statoPartita.getTavolo().getPila().getCima();
		ArrayList<Carta> carteGiocate= new ArrayList<Carta>();
		
		//Gestione della risposta Stacking
		if(statoPartita.getAccomulatorePenalita()>=2 && statoPartita.getRegole().getStacking()) {
			for (Carta carta: manoBot) {
				if ((carta.getValore().equals(Valori.DRAW_TWO) || carta.getValore().equals(Valori.WILD_DRAW_FOUR))&&
					cimaPila.getValore().equals(carta.getValore())) {
						carteGiocate.add(carta);
						return carteGiocate;
				}
			}
		}
		
		
		//Visto che il Bot Aggressivo predilige le carte speciali verrano giocate prima queste senza pensarci.
		
		//Controlla se è presente una carta speciale in mano, se si la gioca
		for(Carta carta:manoBot) {
			if(statoPartita.getRegole().mossaValida(carta, cimaPila)&& !carta.isNumero()) {
					carteGiocate.add(carta);
					return carteGiocate;
			}
		}
		
		//Gestione delle carte giocate nel caso in cui la regola Number Rush è attiva
		//Verrà giocato solo se il bot non ha carte speciali in mano
		if(statoPartita.getRegole().getNumberRush()) carteGiocate= this.numberRush(manoBot, cimaPila, statoPartita);
		if(!carteGiocate.isEmpty())return carteGiocate;
		
		
		//Dopo tutti i vari controlli gioca la prima carta possibile, altrimenti non giocherà nulla e pesca
		for(Carta carta:manoBot) {
			if(statoPartita.getRegole().mossaValida(carta, cimaPila)) {
				carteGiocate.add(carta);
				return carteGiocate;
			}
		}
		
		
		return carteGiocate;
	}
	
	/**
	 * Gestisce la scelta delle carte da giocare in caso di Number Rush attivo.
	 * @param manoBot
	 * @param cimaPila
	 * @return carteRush, se ci sono carte cumulabili returna una lista con le carte dentro, altrimenti una lista vuota
	 */
	private ArrayList<Carta> numberRush(ArrayList<Carta> manoBot, Carta cimaPila, Partita partita){
		//Creo la lista di carte per il Number Rush
		ArrayList<Carta> carteRush = new ArrayList<Carta>();
		//Per ogni carta che il bot ha in mano vedo se è possibile fare number rush
		for(Carta rush :manoBot) {
			if(rush.getColore().equals(Colori.JOLLY)) continue; //Se trovo una carta jolly la devo skippare 
			if(partita.getRegole().mossaValida(rush, cimaPila) && rush.isNumero()) {
				carteRush.add(rush);//Aggiungo la carta rush se è valida
				for(Carta carta: manoBot) {
					
					//Se trovo una carta con lo stesso valore della carta rush l'aggiungo alle carte giocabili
					if(carta!=rush && carta.getValore().equals(rush.getValore())) {
						carteRush.add(carta);
					}				
				}
				
				//Se la lista di carte giocabili è maggiore uguale a 2 restituisco le carte, altrimenti pulisco la lista di carte giocabili e passo alla prossima
				if(carteRush.size()>=2) return carteRush; else carteRush.clear();
						}
					}
				
		
		return carteRush;
	}
}



