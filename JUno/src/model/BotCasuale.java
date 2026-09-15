package model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Rappresenta la struttura del BotCasuale, gestisce le scelte in base a parametri definiti dall'interfaccia 
 * StrategiaBot.
 */
public class BotCasuale implements StrategiaBot{
	
	
	//Creo il campo statico che si occuperà del RNG del bot
	private static Random random = new Random();
	
	/**
	 * Il metodo decide se il bot dovrà contestare la challenge col 50% di probabilità.
	 * @return true se la probabilità è positiva, false altrimenti
	 */
	public boolean decisioneChallenge(ArrayList<Carta> manoBot, Partita statoPartita) {
		int valore=random.nextInt(100);
		if(valore<50) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * Il metodo restituisce un colore col 25% di possibilità per ognuno
	 * @param manoBot, la mano del bot in quel momento
	 * @return scelte, il colore estratto casualmente
	 */
	public Colori scegliColore(ArrayList<Carta> manoBot) {
		Colori[] scelte= {Colori.ROSSO, Colori.BLU, Colori.VERDE, Colori.GIALLO};
		return scelte[random.nextInt(scelte.length)];
	}
	
	/**
	 * metodo che restituisce true o false con il 67% di possibilità  per ogni bot
	 * @param statoPartita, lo stato della partita in corso
	 * @return true se il valore è maggiore di 67, false altrimenti
	 */
	public boolean contestazioneJUno(Partita statoPartita) {
		int valore =random.nextInt(100);
		if(valore<67) return true; else return false;
	}
	
	
	
	/**Il bot dichiara JUno solo al 50% di possibilità
	 * Prende la mano solo per vedere se in mano ha una sola carta.
	 */
	public boolean dichiarazioneJUno() {
		int valore=random.nextInt(100);
		if(valore<75) return true; else return false;
	}
	
	
	
	/**
	 * Metodo che si occupa dello scegliere la carta da giocare in base a varie percentuali
	 * @param manoBot, le carte in mano al bot durante il suo turno
	 * @param statoPartita, lo stato della partita in quel momento
	 * @return carteGiocate, una lista contenente le carte che il bot deve giocare in caso di NRush o una altrimenti.
	 */
	public ArrayList<Carta> scegliCarta(ArrayList<Carta> manoBot, Partita statoPartita) {
		
		//Memorizzo la cimaPila per semplificare il codice e creo un ArrayList delle carte che il bot andrà a giocare
		Carta cimaPila= statoPartita.getTavolo().getPila().getCima();
		ArrayList<Carta> carteGiocate= new ArrayList<Carta>();
		
		
		//C'è il 15% di possibilità che il bot peschi anche se ha carte in mano da giocare
		int valore= random.nextInt(100);
		if (valore<15) return carteGiocate;
		
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
		
		//Gestione delle carte giocate nel caso in cui la regola Number Rush è attiva (50% di chance di fare Rush)
		if(statoPartita.getRegole().getNumberRush() && valore<50) {
			carteGiocate= this.numberRush(manoBot, cimaPila, statoPartita);
		}
		if(!carteGiocate.isEmpty())return carteGiocate;
		
		
		//Controlla le mosse valide che possono essere fatte e poi ne sceglie una in modo randomico
		ArrayList<Carta> carteValide= new ArrayList<Carta>();
		for (Carta carta: manoBot) {
			if(statoPartita.getRegole().mossaValida(carta, cimaPila)) {
				carteValide.add(carta);
			}
		}
		if(!carteValide.isEmpty()) carteGiocate.add(carteValide.get(random.nextInt(carteValide.size())));
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
