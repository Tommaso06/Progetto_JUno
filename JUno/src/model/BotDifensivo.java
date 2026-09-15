package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Comparator;
import java.util.Collections;

/**
 * Rappresenta la struttura del BotAggressivo, gestisce le scelte in base a parametri definiti dall'interfaccia 
 * StrategiaBot.
 */
public class BotDifensivo implements StrategiaBot {  
	//Creo il campo statico che si occuperà del RNG del bot
		private static Random random = new Random();
	
	/**
	 * Metodo che gestisce la scelta del colore in caso di carta Jolly giocata in base alla propria mano
	 * @param manoBot, Le carte in mano al bot al momento della scelta del colore
	 * @return contaColori, il colore più presente in mano al bot
	 */
	public Colori scegliColore(ArrayList<Carta> manoBot) {
		//Creo una Map per contare i colori e vedere quale è il colore dominante della mano del bot
		HashMap<Colori, Integer> contaColori= new HashMap<Colori, Integer>(); 
		
		
		//Itero sulla mano del bot per contare i colori in mano, ignorando i jolly
		for (Carta carta:manoBot) {
			if (carta.getColore().equals(Colori.JOLLY)) continue;
			contaColori.merge(carta.getColore(), 1, Integer::sum); //Controlla se il colore è già una K della HashMap, se esiste
			// aumenta il valore di uno il counter del colore altrimenti crea la chiave e imposta il valore a 1
		}
		
		//Gestisco il caso in cui non ho colori in mano ma solo jolly
		if(contaColori.isEmpty()) {
			Colori[] scelte= {Colori.ROSSO, Colori.BLU, Colori.VERDE, Colori.GIALLO};
			int indiceCasuale= random.nextInt(4);
			return scelte[indiceCasuale];
		}
		
		//Scegli il colore da in base a quello dominante, in caso di parità verrà scelto il primo registrato
		Colori coloreScelto= Collections.max(
			    contaColori.entrySet(), 
			    HashMap.Entry.comparingByValue()
			).getKey();
		
		return coloreScelto;
	}
	
	/**
	 * @param manoBot  la mano del bot al momento della decisione
	 * @param statoPartita lo stato della partita al momento della decisione
	 */
	public boolean decisioneChallenge(ArrayList<Carta> manoBot, Partita statoPartita) {
		
		int puntiMano=0;
		for (Carta carta: manoBot) {
			/*
			 * Conto i punti che il bot ha in mano per capire quanto la sua mano sia pesante perché perdere la challenge conn
			 * la mano pesante creerà solo ulteriori danni in vista del punteggio finale
			 */
			puntiMano+=carta.getValore().getPunti(); 
		}
		if (puntiMano>=60) return false;
		
		//Se l'avversario ha 8 carte o più, molto probabilmente sta bluffando
		if(statoPartita.getGiocatorePrecedente().getMano().size()>=8) {
			return true;
		}
		return false;
	}
	
	
	/**Il bot andrà a contestare col 75% di possibilità, questo perché un giocatore difensivo è attento alle giocate altrui
	 * @param statoPartita lo stato della partita in quel momento
	 * @return true in base alla probabilità sopracitata, false altrimenti
	 */
	public boolean contestazioneJUno(Partita statoPartita) {
		int valore= random.nextInt(100);
		if(valore<75) return true; else return false;
	}
	
	
	
	/**Il bot dichiara JUno al 95% di possibilità
	 * Prende la mano solo per vedere se in mano ha una sola carta.
	 * @return true in base alla variabile, false altrimenti
	 */
	public boolean dichiarazioneJUno() {
		int valore= random.nextInt(100);
		if(valore<95) return true; else return false;
		}

	/**Il metodo decide le carte che il bot andrà a giocare e se le giocherà
	 * @param manoBot le carte in mano al bot durante il suo turno
	 * @param statoPartita lo stato della partita durante il turno del bot
	 * @return carteGiocate una lista di carte valide da giocare o una lista vuota se il bot vuole pescare.
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
				
		
		//Conto le mosse valide disponibili 
		int contatoreMosse =0;
		int contatoreDraw=0;
		for(Carta carta: manoBot) {
			//Differenzio le mosse del +2 e +4 per controlli futuri 
			if(carta.getValore().equals(Valori.DRAW_TWO) || carta.getValore().equals(Valori.WILD_DRAW_FOUR)) {
				contatoreDraw+=1;
				continue;
			}
			if(statoPartita.getRegole().mossaValida(carta, cimaPila)) contatoreMosse+=1;
		}
		
		//se ho poche mosse e più di tre carte, il bot pescherà cosi da avere tante mosse per il late game
		if(contatoreMosse+contatoreDraw<=2 && manoBot.size()>=3) return carteGiocate;
		
		//Se le uniche mosse disponibili sono le carte Draw e ne ho meno di 2, pesco, in questo posso difendermi
		//quasi sicuramente allo stacking
		if(contatoreMosse+contatoreDraw==contatoreDraw && contatoreDraw<=2 
			&& statoPartita.getRegole().getStacking()) return carteGiocate;
		
		//Gestione delle carte giocate nel caso in cui la regola Number Rush è attiva
		//Verrà giocato solo se il bot non ha carte speciali in mano
		if(statoPartita.getRegole().getNumberRush()) carteGiocate= this.numberRush(manoBot, cimaPila, statoPartita);
		if(!carteGiocate.isEmpty())return carteGiocate;
		
		ArrayList<Carta> mosseValide=new ArrayList<Carta>();
		for(Carta carta: manoBot) {
			
			//Il bot salterà il +4 se ha altre mosse disponibili
			if(contatoreMosse>0 && carta.getValore().equals(Valori.WILD_DRAW_FOUR)) continue;
			
			
			//Se la carta è valide l'aggiungo alla lista di mosse valide
			if(statoPartita.getRegole().mossaValida(carta, cimaPila)) mosseValide.add(carta);
			
		}
		//In caso in cui non abbia mosse valide, torno la lista vuota e il bot pescherà
		if(mosseValide.isEmpty()) return carteGiocate;
		
		//Creo una Map per contare i colori e vedere quale è il colore dominante fra le mosse valide
		HashMap<Colori, Integer> contaColori= new HashMap<Colori, Integer>(); 
				
				
		//Itero sulla mosse valide
		for (Carta carta:mosseValide) contaColori.merge(carta.getColore(), 1, Integer::sum); 
		//Controlla se il colore è già una K della HashMap, se esiste
		// aumenta il valore di uno il counter del colore altrimenti crea la chiave e imposta il valore a 1
		
		
		//Estraggo il colore dominant nelle mosse valide
		Colori coloreDominante = Collections.max(
			    contaColori.entrySet(), 
			    HashMap.Entry.comparingByValue()
			).getKey();
		
		//Verrà giocata la carta col colore più frequente in mano
		for(Carta carta:mosseValide) {
			if(carta.getColore().equals(coloreDominante)) {
				carteGiocate.add(carta);
				break;
			}
		}
		
		return carteGiocate;
	}
	
	
	/**
	 * Gestisce la scelta delle carte da giocare in caso di Number Rush attivo.
	 * @param manoBot la mano del bot durante il suo turno
	 * @param cimaPila la carta in cima alla pila
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
