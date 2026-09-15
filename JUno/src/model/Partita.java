package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Classe principale che gestisce lo stato e la logica della partita di JUno.
 * Funge da Model centrale, estendendo {@link Observable}: 
 * ad ogni cambiamento significativo di stato, notifica gli Observer
 * affinché aggiornino l'interfaccia grafica.
 * Gestisce turni, giocatori, tavolo, regole applicate e lo storico delle mosse.
 */
public class Partita extends Observable implements Serializable{
	
    private Giocatore[] giocatori;
    private Tavolo tavolo;
    
    private int turnoCorrente = 0; 
    private int sensoCorrente = 1; // 1 orario -1 antiorario 
    
    private int numGiocatori;
    private int numUmani;
    private int numBot;
    
    private final Regole regole;
    private final ModalitàGioco modalita;
    private Giocatore vincitore;
    private StoricoPartita storico; 
    private int accumulatorePenalita;  // per lo stacking
    
    /**
     * Costruttore che inizializza la partita, il tavolo, e crea i giocatori.
     * @param numUmani, il numero di giocatori fisici.
     * @param numBot, il numero di giocatori controllati dal computer.
     * @param nomi, l'array contenente i nomi dei giocatori (sia umani che bot).
     * @param strategieBot, l'array contenente tutte le strategie già nel formato completo.
     * @param modalita, la modalità di gioco scelta (Classica o a Punti).
     * @param regole, l'istanza contenente il regolamento e le varianti attive.
     */
    public Partita(int numUmani, int numBot, String[] nomi, StrategiaBot[] strategieBot, ModalitàGioco modalita, Regole regole) {
        this.tavolo = new Tavolo(); 
        this.numUmani = numUmani;
        this.numBot = numBot;
        this.numGiocatori = numBot + numUmani;
        this.giocatori = new Giocatore[numGiocatori];
        this.regole = regole; 
        this.modalita = modalita;
        this.vincitore = null;
        this.storico = new StoricoPartita();
        
        // creazione giocatori umani
        for (int i = 0; i < numUmani; i++) {
            this.giocatori[i] = new GiocatoreUmano(nomi[i]);
        }
        // creazione Bot
        int indiceStrategia = 0;
        for (int i = numUmani; i < this.numGiocatori; i++) {
        	this.giocatori[i] = new GiocatoreAI(nomi[i], strategieBot[indiceStrategia]);
        	indiceStrategia++;
        }
        this.turnoCorrente = 0;
    }
    /**
     * Contesta la mancata dichiarazione di "JUNO" da parte degli avversari.
     * Scorre tutti i giocatori, se qualcuno ha 1 sola carta e non ha dichiarato JUNO, 
     * gli assegna una penalità di 2 carte. Notifica la View se avviene un pescaggio.
     */
    public void chiamaControllo() {
        boolean qualcunoHaPescato = false;
        for (Giocatore giocatore : giocatori) {
            if (giocatore.getMano().size() == 1) {
                if (!this.regole.checkUno(giocatore)) {
                    giocatore.pesca(tavolo);
                    giocatore.pesca(tavolo);
                    qualcunoHaPescato = true;
                    storico.aggiungiMossa(giocatore.getNome() + " non ha dichiarato JUNO, pesca 2 carte");
                }
            }
        }
        // notifica alla GUI per ridisegnare le mani avversarie
        if (qualcunoHaPescato) {
            notificaCambiamento();
        }
    }
      
    /**
     * Gestisce la giocata di una o più carte contemporaneamente da parte del giocatore di turno.
     * Applica le regole di validazione, gestisce lo Stacking, 
     * il Number Rush e applica gli effetti delle carte.
     * @param carteDaGiocare, lista delle carte selezionate per essere giocate.
     * @return {@code true} se la giocata è valida ed è stata eseguita, {@code false} altrimenti.
     */
    public boolean giocaCarte(ArrayList<Carta> carteDaGiocare) { 
        if (carteDaGiocare == null || carteDaGiocare.isEmpty()) return false;
        
        Carta cimaPila = this.tavolo.getPila().getCima();
        Giocatore chiHaGiocato = getGiocatoreAttuale(); 
        
        if (this.regole.getColoreProvvisorio() != null) {
            this.regole.setColoreChallenge(this.regole.getColoreProvvisorio());
        } else {
            this.regole.setColoreChallenge(cimaPila.getColore());
        }
        
        // -- CONTROLLO STACKING 
        if (this.accumulatorePenalita > 0) {
            // con un debito non puoi fare Number Rush, devi scartare 1 sola carta
            if (carteDaGiocare.size() > 1) return false; 
            Carta carta = carteDaGiocare.get(0);
            
            // controlla se la carta è valida per difendersi
            if (!this.regole.checkGiocataStacking(carta, cimaPila)) {
                return false; 
            }
            if (carta.getColore() != Colori.JOLLY) {
                this.regole.setColoreProvvisorio(null);
            }
            chiHaGiocato.giocaCarta(carta, this.tavolo.getPila()); 
            storico.aggiungiMossa(chiHaGiocato.getNome() + " risponde allo Stacking con " + carta.toString());
            this.regole.applicaCarta(carta, this); 
            if (carta.getColore() != Colori.JOLLY) {
                this.passaTurno();
                this.gestisciPenalitaStacking(); 
            }
            notificaCambiamento();
            return true; 
        }
        
        // -- GIOCATA NORMALE SINGOLA
        if (carteDaGiocare.size() == 1) {
            Carta carta = carteDaGiocare.get(0);

            if (!this.regole.mossaValida(carta, cimaPila)) {
                return false; 
            }         
            if (carta.getColore() != Colori.JOLLY) {
                this.regole.setColoreProvvisorio(null);
            }
            chiHaGiocato.giocaCarta(carta, this.tavolo.getPila()); 
            storico.aggiungiMossa(chiHaGiocato.getNome() + " ha giocato " + carta.toString());
            this.regole.applicaCarta(carta, this); 
            
        } 
        // -- NUMBER RUSH --
        else {
            if (!this.regole.checkNumberRush(carteDaGiocare, cimaPila)) {
                return false;
            }
            Carta ultimaCarta = carteDaGiocare.get(carteDaGiocare.size() - 1);
            if (ultimaCarta.getColore() != Colori.JOLLY) {
                this.regole.setColoreProvvisorio(null);
            }
            StringBuilder sb = new StringBuilder();
            for (Carta carta : carteDaGiocare) {
                sb.append(carta.toString()).append(" ");
            }
            storico.aggiungiMossa(chiHaGiocato.getNome() + " ha giocato " + sb.toString().trim());
            for (Carta carta : carteDaGiocare) {
                chiHaGiocato.giocaCarta(carta, this.tavolo.getPila()); 
                this.regole.applicaCarta(carta, this); 
            }
            
        }
        storico.aggiungiMossa(chiHaGiocato.getNome()+" ha "+chiHaGiocato.getMano().size()+" carte in mano");

        // controllo il vincitore, aggiungo i punti
        this.vincitore = modalita.winRound(chiHaGiocato); 
        aggiungiPuntiVincitore();
        // resetto la dichiarazione JUno se un giocatore ha più di una carta
        if (chiHaGiocato.getMano().size() != 1) {
            chiHaGiocato.setDichiarazioneJuno(false);
        }
        
        // passo il turno e controllo lo stacking solo se non è un Jolly 
        // il Jolly sospende il turno per la scelta del colore
        Carta ultimaCarta = carteDaGiocare.get(carteDaGiocare.size() - 1);
        if (ultimaCarta.getColore() != Colori.JOLLY) {
            this.passaTurno(); 
            this.gestisciPenalitaStacking();
        }
        notificaCambiamento(); 
        return true; 
    }
    
    /** Aggiorna matematicamente l'indice del turno corrente in base al senso di gioco. */
    private void passaTurno() {
        turnoCorrente = (turnoCorrente + sensoCorrente + numGiocatori) % numGiocatori;
    }
    
    /**
     * Gestisce la penalità dello Stacking: verifica se il giocatore di turno 
     * ha le carte per rispondere al debito, altrimenti lo costringe a pescare l'accumulo.
     */
    private void gestisciPenalitaStacking() {
        if (accumulatorePenalita > 0) {
            Giocatore nuovoGiocatore = getGiocatoreAttuale();
            Carta cimaPila = this.tavolo.getPila().getCima();
            if (!this.regole.checkStackingCarte(nuovoGiocatore, cimaPila)) {
                pescaCarta(); 
            }
        }
    }
    
    /** Gestisce l'effetto della carta Skip, saltando il turno del giocatore successivo. */
    public void skipTurno() {
        passaTurno();
    }
    
    /** Gestisce l'effetto della carta Reverse, invertendo il senso di gioco attuale. */
    public void reverseTurno() {
        if(sensoCorrente == 1) { this.sensoCorrente = -1; }
        else if(sensoCorrente == -1) { this.sensoCorrente = 1; }
    }
 
    /**
     * Imposta il nuovo colore provvisorio dopo la giocata di una carta Jolly.
     * @param nuovoColore, il colore scelto dal giocatore.
     */
    public void cambioColore(Colori nuovoColore) {
        if(nuovoColore != null && nuovoColore != Colori.JOLLY) {
            this.regole.setColoreProvvisorio(nuovoColore);
            storico.aggiungiMossa("Il colore del gioco è stato cambiato in " + nuovoColore);
            // se il giocatore attuale non è sotto challenge
            if (!getGiocatoreAttuale().getChallenge()) {
                this.passaTurno();
                this.gestisciPenalitaStacking();
            }
            notificaCambiamento();
        }
    }
    
    /**
     * Interagisce con il giocatore che subisce un WILD_FOUR.
     * Applica le penalità in base alla scelta di contestare o meno.
     * @param vuoleContestare {@code true} se il giocatore decide di fare Challenge, {@code false} altrimenti.
     */
    public void DecisioneChallenge(boolean vuoleContestare) {
        if (getGiocatoreAttuale().getChallenge()) {
            
            getGiocatoreAttuale().setChallenge(false); // rimuove il flag della sfida
            
            Giocatore accusato = this.regole.getAccusato();
            Giocatore sfidante = getGiocatoreAttuale();
            if (vuoleContestare) {
                boolean haBarato = this.regole.challenge(accusato);
                if (haBarato) {
                    storico.aggiungiMossa("Challenge riuscito: " + accusato.getNome() + " pesca 4 carte");
                    for (int i=0; i<4; i++) { accusato.pesca(tavolo); }
                } else {
                    storico.aggiungiMossa("Challenge fallito: " + sfidante.getNome() + " pesca 6 carte");
                    for (int i=0; i<6; i++) { sfidante.pesca(tavolo); }
                    this.passaTurno();
                }
            } 
            else { 
                storico.aggiungiMossa("Challenge rifiutato: " + sfidante.getNome() + " pesca 4 carte");
                for (int i=0; i<4; i++) { this.getGiocatoreAttuale().pesca(tavolo); }
                this.passaTurno();
            }
            // resetta il colore del challenge a fine operazione
            this.regole.setColoreChallenge(null);
            notificaCambiamento();
        }
    }

    /**
     * Gestisce e convalida la dichiarazione di JUNO del giocatore attuale.
     * @param confermata {@code true} se il giocatore ha premuto il bottone JUNO.
     */
    public void dichiarazioneUno(boolean confermata) {
        int dimensioneMano = this.getGiocatoreAttuale().getMano().size();
        if(confermata && !this.getGiocatoreAttuale().getDichiarazioneJuno()) {
            this.getGiocatoreAttuale().setDichiarazioneJuno(true);
            storico.aggiungiMossa(getGiocatoreAttuale().getNome() + " dichiara J-UNO");
            notificaCambiamento();
        }
    }
    
    /**
     * Calcola il punteggio totale delle carte rimaste nelle mani degli avversari 
     * e lo assegna al vincitore del round.
     */
    public void aggiungiPuntiVincitore() {
        if (this.vincitore == null) return;
        int puntiTotaliRound = 0;
        for (Giocatore giocatore : giocatori) {
            puntiTotaliRound += giocatore.getPuntiMano();
        }        
        this.vincitore.aggiungiPunti(puntiTotaliRound); 
    }
    
    /** Distribuisce la mano iniziale di 7 carte a tutti i giocatori. */
    public void inizializzaPartita() {
        for (int i=0; i<this.numGiocatori; i++) {
            for (int k=0; k<7; k++) {
                giocatori[i].pesca(tavolo);
            } 
        }
        notificaCambiamento();
    }
    
    /**
     * Inizializza un nuovo round (solo per la Modalità a Punti).
     * Resetta tavolo, mani e storici, per poi distribuire 7 nuove carte.
     */
    public void inizializzaNuovoRound() {
        
    	this.turnoCorrente = 0;				// riparte dal giocatore iniziale
    	this.sensoCorrente = 0;				// ripristina il senso orario standard
        this.vincitore = null;				// resetta il vincitore del round
        this.accumulatorePenalita = 0;		//azzera i debiti di stacking
        this.regole.setColoreProvvisorio(null);
        this.regole.setColoreChallenge(null);
        this.storico.pulisciStorico();		// pulisce lo storico delle mosse
        
        this.tavolo = new Tavolo();
        
        for (int i = 0; i < this.numGiocatori; i++) {
            this.giocatori[i].getMano().clear(); 
            this.giocatori[i].setDichiarazioneJuno(false); 
            
            for (int k = 0; k < 7; k++) {
                this.giocatori[i].pesca(tavolo);
            } 
        }
        notificaCambiamento();
    }
    
    /**
     * Costringe il giocatore attuale a pescare una carta (o l'accumulo intero in caso di stacking attivo),
     * dopodiché rimuove l'eventuale dichiarazione di JUno e passa il turno al successivo.
     */
    public void pescaCarta() {
        Giocatore giocatoreAttuale = getGiocatoreAttuale();
        if (accumulatorePenalita > 0) {
            for (int i = 0; i < accumulatorePenalita; i++) {
                giocatoreAttuale.pesca(this.tavolo);
            }
            storico.aggiungiMossa(giocatoreAttuale.getNome() + " ha pescato " + accumulatorePenalita + " carte");
            accumulatorePenalita = 0;
        } else {
            giocatoreAttuale.pesca(this.tavolo);
            storico.aggiungiMossa(giocatoreAttuale.getNome() + " ha pescato una carta");
            storico.aggiungiMossa(giocatoreAttuale.getNome() +" ha " + giocatoreAttuale.getMano().size() +" carte in mano");
        }
        giocatoreAttuale.setDichiarazioneJuno(false);
        
        
        this.passaTurno();
        notificaCambiamento();
    }

    /** Segna lo stato del Model come "modificato" e notifica la View tramite Observer. */
    private void notificaCambiamento() {
        setChanged();
        notifyObservers(this);
    }
    
    // --- GETTER & SETTER ---
    
    /** Restituisce il giocatore che deve effettuare la mossa in questo turno. 
     * @return il Giocatore attuale. 
     */
    public Giocatore getGiocatoreAttuale() { return giocatori[turnoCorrente]; }
    
    /** Calcola e restituisce il giocatore del turno successivo. 
     * @return il Giocatore successivo. 
     */
    public Giocatore getGiocatoreSuccessivo() { return giocatori[(turnoCorrente + sensoCorrente + numGiocatori) % numGiocatori]; }
    
    /** Calcola e restituisce il giocatore del turno precedente.
     *  @return il Giocatore precedente. 
     */
    public Giocatore getGiocatorePrecedente() { return giocatori[(turnoCorrente - sensoCorrente + numGiocatori) % numGiocatori]; }
    
    /** Restituisce l'array contenente tutti i giocatori. 
     * @return giocatori, Array di Giocatori. 
     */
    public Giocatore[] getGiocatori() { return this.giocatori; }
    
    /** Restituisce l'oggetto che gestisce mazzo e pila degli scarti. 
     * @return il Tavolo corrente. 
     */
    public Tavolo getTavolo() { return this.tavolo; }
    
    /** Restituisce l'eventuale vincitore della partita/round. 
     * @return vincitore,o null se in corso. 
     */
    public Giocatore getVincitore() { return this.vincitore; }
    
    /** Restituisce il numero totale dei giocatori. 
     * @return numGiocatori. 
     */
    public int getNumGiocatori() { return this.numGiocatori; }
    
    /** Restituisce l'oggetto che incapsula le regole attive. 
     * @return Regole.
     */
    public Regole getRegole() { return this.regole; }
    
    /** Restituisce la modalità di gioco attiva. 
     * @return modalità. 
     */
    public ModalitàGioco getModalita() { return this.modalita; }
    
    /** Verifica se è attiva la Modalità "a Punti". 
     * @return true se attiva, false se "Classica". 
     */
    public boolean isModalitaAPunti() { return this.modalita instanceof ModalitàAPunti; }
    
    /** Restituisce l'oggetto che tiene traccia delle mosse della partita. 
     * @return storico. 
     */
    public StoricoPartita getStorico() { return this.storico; }
    
    /** Restituisce la lista in formato stringa di tutte le mosse effettuate nel round. 
     @return Lista di eventi. 
    */
    public ArrayList<String> getCronologiaMosse() { return storico.getMosse(); }
    
    /** Restituisce il valore della penalità per lo Stacking
     * @return Quantità di penalità
     */
    public int getAccomulatorePenalita() {return this.accumulatorePenalita;}
    
    /** Somma le carte di penalità (Stacking) da far pescare al giocatore successivo. 
     * @param penalita, il numero di carte da aggiungere al debito. 
     */
    public void aggiungiPenalita(int penalita) { accumulatorePenalita += penalita; }
    
    /** Restituisce il numero di giocatori umani. 
     * @return numUmani. 
     */
    public int getNumUmani() { return this.numUmani; }
    
    /** Restituisce il numero di giocatori gestiti dal bot. 
     * @return numBot. 
     */
    public int getNumBot() { return this.numBot; }
}