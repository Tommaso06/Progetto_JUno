package controller;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.View;

import model.BotDifensivo;
import model.BotAggressivo;
import model.BotCasuale;
import model.Carta;
import model.Colori;
import model.Giocatore;
import model.GiocatoreAI;
import model.ModalitàClassica;
import model.ModalitàAPunti;
import model.ModalitàGioco;
import model.Partita;
import model.Regole;
import model.StrategiaBot;
import utility.GestoreSalvataggi;
import view.FinestraPrincipale;
import view.PannelloConfigurazione;
import view.PannelloNomi;


/**
 * Classe GameController.
 * Fa da intermediario (secondo il pattern MVC) tra la View e il Model, 
 * gestendo gli input dell'utente e aggiornando l'interfaccia.
 * Implementa {@link Observer} per rimanere in ascolto dei cambiamenti generati dalla Partita.
 */
public class GameController implements Observer {
    
    private Partita partita; 
    private FinestraPrincipale view; 
    private boolean giocoInPausa = false;
    private ArrayList<Carta> carteSelezionate = new ArrayList<>();
    private javax.swing.Timer timerBot;
    
    /**
     * Costruttore della classe GameController.
     * Inizializza l'interfaccia grafica creando la finestra principale
     * e mostra la schermata del menu iniziale.
     */
    public GameController() {
        this.view = new FinestraPrincipale(this);
        this.view.mostraMenu();
    }
    
    /**
     * Mostra il pannello della configurazione per la scelta del numero 
     * di giocatori e delle regole della partita.
     */
    public void mostraConfigurazione() {
        this.view.mostraConfigurazione();
    }
    
    /**
     * Distrugge l'eventuale partita in corso, imposta la pausa del gioco su false,
     * e riporta l'utente al menu principale.
     */
    public void mostraMenu() {
        this.partita = null; // distrugge la partita in corso
        this.giocoInPausa = false;
        this.view.mostraMenu();
    }
    
    /**
     * Interrompe l'esecuzione del programma e chiude il gioco.
     */
    public void esciDalGioco() {
        System.exit(0); 
    }
    
    /**
     * Apre il pannello delle impostazioni.
     * Salva lo stato del gioco, per poter ripristinare la schermata corretta alla chiusura.
     * @param dalTavolo {@code true} se apriamo le impostazioni durante la partita.
     * {@code false} se le apriamo dal menu principale.
     */
    public void mostraImpostazioni(boolean dalTavolo) {
        this.giocoInPausa = dalTavolo;
        this.view.mostraImpostazioni();
        this.view.getPannelloImpostazioni().gestisciBottoni(dalTavolo);    }
    
    /**
     * Chiude il pannello delle impostazioni e ripristina la schermata precedente.
     * Se si era in partita, rimuove la pausa e se è il turno di un bot
     * ne riavvia il timer di pensiero.
     */
    public void chiudiImpostazioni() {
        if (giocoInPausa) {
            this.view.mostraTavolo();
            
            //tolgo la pausa
            this.giocoInPausa = false;
            
            // se toccava ad un bot e c'è la partita si fa rigiocare
            if (partita != null && partita.getGiocatoreAttuale().isBot()) {
                faiGiocareBot(); 
            }
        } else {
            this.view.mostraMenu();
        }
    }
    
    /**
     * Richiede alla view di mostrare la schermata della classifica post Partita.
     * @param partita L'oggetto {@link Partita} contenente i risultati e i punteggi finali.
     */
    public void mostraClassifica(Partita partita) {
        this.view.mostraClassifica(partita);
    }
    
    /**
     * Avvia la partita utilizzando una funzione interna di raccolta dati.
     */
    public void avviaPartita() {
        raccogliDatiEAvvia();
    }
    
    /**
     * Avvia un nuovo round (utilizzato nella modalità a punti)
     * richiamando l'inizializzazione del Model e mostrando di nuovo il tavolo.
     */
    public void avvioNuovoRound() {
        partita.inizializzaNuovoRound();
        this.view.mostraTavolo();
    }

    /**
     * Controlla la validità numerica dei partecipanti selezionati e ordina
     * al {@link PannelloNomi} di generare dinamicamente le caselle di testo necessarie,
     * mostrando la schermata di inserimento nomi.
     */
    public void mostraSchermataNomi() {
    	
    	// prendo i dati dall pannelloConfigurazione
    	PannelloConfigurazione config = view.getPannelloConfigurazione();
        int umani = (Integer) config.getComboUmani().getSelectedItem();
        int bot = (Integer) config.getComboBot().getSelectedItem();

        // genero il pannello di scelta dei nomi passando prima gli umani poi i bot
        this.view.getPannelloNomi().generaCampiNomi(umani, bot);
        
        // mostro la schermata dei nomi
        this.view.mostraNomi();
    }
    
    /**
     * Mostra la schermata dello storico se una partita è attualmente istanziata.
     */
    public void mostraStorico() {
        if (partita != null) {
            view.mostraStorico(partita);
        }
    }

    /**
     * Chiude la schermata dello storico e riporta alla classifica finale.
     */
    public void chiudiStorico() {
    	if (partita != null) {
    		view.mostraClassifica(partita); 
    	}
        
    }
    
    /**
     * Raccoglie tutti i parametri inseriti dall'utente (giocatori, nomi, modalità, regole),
     * istanzia la ({@link Partita}), si registra come Observer 
     * e avvia la distribuzione delle carte.
     */
    private void raccogliDatiEAvvia() {
    	
    	// se avevamo giocoInPausa torna false
    	this.giocoInPausa = false;
        PannelloConfigurazione configurazione = view.getPannelloConfigurazione();
        PannelloNomi pannelloNomi = view.getPannelloNomi();
        
        // prendo i dati dal pannello dei nomi e strategie
        String[] nomi = pannelloNomi.estraiNomi();
        String[] stringheStrategieBot = pannelloNomi.estraiDifficoltaBot();
        

        // se i nomi che ricevo sono null fermo tutto
        if (nomi == null) {
            return; 
        }
        
        // prendo il numero di umani e di bot 
        int numUmani = (Integer) configurazione.getComboUmani().getSelectedItem();
        int numBot = (Integer) configurazione.getComboBot().getSelectedItem();
        
     // trasformo le stringhe in stretegie vere e proprie
        
        StrategiaBot[] strategieBot = new StrategiaBot[numBot];
        for (int i = 0; i < numBot; i++) {
            if (stringheStrategieBot[i].equals("Aggressivo")) {
                strategieBot[i] = new BotAggressivo();
            } else if (stringheStrategieBot[i].equals("Casuale")) {
                strategieBot[i] = new BotCasuale();
            } else {
                strategieBot[i] = new BotDifensivo();
            }
        }
        
        // instanzio la partita CLASSICA o A PUNTI in base alla scelta
        ModalitàGioco modalita;
        if (configurazione.getRadioPunti().isSelected()) {
        	// raccolgo la soglia vittoria scelta dall'utente e avvio partita a punti
            int puntiVittoria = (Integer) configurazione.getComboPuntiVittoria().getSelectedItem();
            modalita = new ModalitàAPunti(puntiVittoria); 
            
        } else { //altrimenti avvio partita classica
            modalita = new ModalitàClassica();
        }
        
        // raccolgo lo stato delle regole alternative
        boolean Stacking = configurazione.getCheckStacking().isSelected();
        boolean NumberRush = configurazione.getCheckNumberRush().isSelected();
        boolean SoloBot = configurazione.getCheckSimulazioneBot().isSelected();
        Regole regole= new Regole(Stacking, NumberRush, SoloBot);
        
        // instanzio la partita usando i dati raccolti
        this.partita = new Partita(numUmani, numBot, nomi,strategieBot, modalita, regole);
        
        // aggancio l'observer per ricevere le notifiche di modifica del model
        this.partita.addObserver(this);
        
        // cambio della schermata per mostrare il tavolo da gioco
        this.view.mostraTavolo();
        
        // distribuzione delle carte iniziali e posizionamento della prima carta scarti
        this.partita.inizializzaPartita(); 
    }

    
    
    /**
     * Permette di pescare una carta tramite il metodo pescaCarta del model.
     */
    public void azionePesca() {
        if(partita != null) {
            partita.pescaCarta();
        }
    }
    
    /**
     * Permette di selezionare una o più carte.
     * Se Number Rush è SPENTO: Gioca immediatamente la carta.
     * Se Number Rush è ACCESO: Aggiunge/Rimuove la carta dalle selezionate.
     * @param carta,  la carta su cui il giocatore ha cliccato.
     */
    public void azioneGiocaCarta(Carta carta) {
        if(partita == null) return;
        
        // --NUMBER RUSH spento
        // giocata classica
        if (!partita.getRegole().getNumberRush()) {
            ArrayList<Carta> singolaCarta = new ArrayList<>();
            singolaCarta.add(carta);
            // prima provo a giocare la mossa nel model
            boolean valida = partita.giocaCarte(singolaCarta);
            // se non è valida
            if (!valida) {
                view.getPannelloTavolo().mostraErroreMossaNonValida();
                view.getPannelloTavolo().aggiorna(partita);
                return;
            } else {
            	//se la mossa è valida
                // mostro eventuali popup
                if (carta.getColore() == Colori.JOLLY && partita.getVincitore() == null) {
                    view.getPannelloTavolo().mostraPannelloColori();
                }

            }
            return;
        }
        
        // -- NUMBER RUSH -- acceso 
        // utilizzo la modalità selezione delle carte
        if (carteSelezionate.contains(carta)) {
            carteSelezionate.remove(carta); // deseleziono la carta che tornerà giù
        } else {
            carteSelezionate.add(carta); // seleziono la carta che si rialzerà
        }
        
        // aggiorna la vista per mostrare le carte alzate/abbassate
        view.getPannelloTavolo().aggiorna(partita);
    }
    
    
    /**
     * Metodo associato al bottone "Gioca" sul tavolo (visibile solo se Number Rush è attivo).
     * Invia la lista di tutte le carte attualmente selezionate al Model per la validazione multipla.
     */
    public void confermaGiocata() {
        if (carteSelezionate.isEmpty() || partita == null) return;
        
        // salvo la carta che finirà in cima, per i controlli successivi
        Carta ultimaCarta = carteSelezionate.get(carteSelezionate.size() - 1);
        
        // prima provo a far validare e giocare le carte al model
        boolean valida = partita.giocaCarte(carteSelezionate);
        
        // se la giocata non è valida
        if (!valida) {
        	view.getPannelloTavolo().mostraErroreMossaNonValida();
            carteSelezionate.clear(); // resetto la selezione
            view.getPannelloTavolo().aggiorna(partita); // le carte tornano giù
            return;
        } else {
        	// se la giocata è valida
            carteSelezionate.clear(); // svuoto per il turno successivo
            
            // mostro i vari popup
            if (ultimaCarta.getColore() == Colori.JOLLY) {
                view.getPannelloTavolo().mostraPannelloColori();
            }
            if (partita.getGiocatoreAttuale().getChallenge() && !partita.getGiocatoreAttuale().isBot()) {
                view.getPannelloTavolo().mostraDecisioneChallenge();
            }
        }
    }
    
    /**
     * Comunica al Model il nuovo colore scelto dall'utente tramite la View,
     * solitamente richiamato in seguito alla giocata di una carta Jolly.
     * @param colore, il nuovo colore scelto dal giocatore.
     */
    public void cambioColore(Colori colore) {
        partita.cambioColore(colore);
    }
    
    /**
     * Gestisce il click del bottone "JUno", dichiarando che il giocatore attuale
     * ha una sola carta in mano, mettendolo al sicuro dalle penalità.
     */
    public void gestisciJUno() {
        partita.dichiarazioneUno(true);
    }
    
    /**
     * Gestisce il click del bottone "Contesta JUno".
     * Invia un segnale al Model per verificare se qualche giocatore
     * avente solo una carta in mano ha dimenticato di dichiarare JUno.
     */
    public void gestisciContestaJUno() {
        partita.chiamaControllo();
    }
    
    /**
     * Gestisce la decisione di un giocatore che sceglie se contestare o meno 
     * un WILD_FOUR subito, delegando la gestione ad un altro metodo.
     * @param vuoleContestare {@code true} se il giocatore ha cliccato "si", 
     * {@code false} se ha cliccato "no".
     */
    public void gestisciChallenge(boolean vuoleContestare) {
        if (partita != null) {
            partita.DecisioneChallenge(vuoleContestare);
        }
    }
    
    /**
     * Utilizzato nella Modalità a Punti per continuare la partita con un 
     * nuovo round, ripristinando il tavolo ma mantenendo i punteggi cumulati.
     */
    public void continuaPartita() {
        if(partita != null) {
            partita.inizializzaNuovoRound(); 
            this.view.mostraTavolo();
        }
    }
    
    /**
     * Gestisce il turno di un Bot, Sfrutta un {@link javax.swing.Timer} per introdurre 
     * un ritardo (4 secondi),e simulare il tempo di pensiero,
     * prima che il bot esegua la sua mossa sul Model.
     */
    public void faiGiocareBot() {
        // se il gioco è in pausa o la partita è finita il bot si ferma subito
        if (giocoInPausa || partita.getVincitore() != null) {
            return; 
        }

        // se c'è già un timer che sta contando, viene fermato
        // per evitare che i bot giochino troppe carte contemporaneamente
        if (timerBot != null && timerBot.isRunning()) {
            timerBot.stop();
        }
        
        // uso la variabile globale timerBot invece di crearne una nuova
        timerBot = new javax.swing.Timer(4000, e -> {
            // ricontrollo nel caso lo stato sia cambiato mentre il timer scorreva
            if (partita == null || giocoInPausa || partita.getVincitore() != null) {
                return;
            }
            
            // se tutto è ok, il bot fa la sua mossa 
            Giocatore giocatoreAttuale = partita.getGiocatoreAttuale();
            
            if (giocatoreAttuale instanceof GiocatoreAI) {
                GiocatoreAI bot = (GiocatoreAI) giocatoreAttuale; // Esegue il downcast
                
                //Gestione del caso in cui il bot subisce un +4
                if(bot.getChallenge()) {
                	partita.DecisioneChallenge(bot.getStrategia().decisioneChallenge(bot.getMano(), partita));
                }
                
                //gestisce la scelta per contestare il JUno mancante di altri giocatori
                if(bot.getStrategia().contestazioneJUno(partita)) partita.chiamaControllo();
                
                //selezione delle carte da giocare da parte del bot
                ArrayList<Carta> carteGiocate = bot.getStrategia().scegliCarta(bot.getMano(), partita);
                
                if (carteGiocate == null ||carteGiocate.isEmpty()) {
                    partita.pescaCarta();
                } else {
//                	System.out.println(bot.getMano().size()-carteGiocate.size());
                	if(bot.getMano().size()-carteGiocate.size()==1)partita.dichiarazioneUno(bot.getStrategia().dichiarazioneJUno());
                    partita.giocaCarte(carteGiocate);
                    
                    // se gioca un jolly, cambia colore immediatamente
                    if (carteGiocate.getFirst().getColore().equals(Colori.JOLLY)) {
                        Colori coloreScelto = bot.getStrategia().scegliColore(bot.getMano());
                        partita.cambioColore(coloreScelto);
                    }
                }
            }           
        });
        
        timerBot.setRepeats(false); 
        timerBot.start();
    }
    
    /**
     * Metodo {@code update} implementato dall'interfaccia {@link Observer}.
     * Viene notificato in automatico dal Model ogni volta che lo stato della 
     * partita cambia (es: giocata una carta, cambio turno, vittoria).
     * Sincronizza la View con i nuovi dati.
     * @param o , l'oggetto Observable che ha generato la notifica (ovvero la {@link Partita}).
     * @param arg, l'argomento facoltativo passato dal Model, in questo caso l'istanza aggiornata della Partita.
     */
    @Override
    public void update(Observable o, Object arg) {
        
        if (arg instanceof Partita) {
            Partita statoAttuale = (Partita) arg;
            
            if (statoAttuale.getVincitore() != null) {
                mostraClassifica(statoAttuale);
            } else {
                view.getPannelloTavolo().aggiorna(statoAttuale);
                
                // se tocca a un umano sotto challenge, apro il popup
                if (statoAttuale.getGiocatoreAttuale().getChallenge() && !statoAttuale.getGiocatoreAttuale().isBot()) {
                    view.getPannelloTavolo().mostraDecisioneChallenge();
                    return; // fermo l'update per aspettare il click dell'utente
                }
                
                // se invece tocca a un bot, chiamo il suo metodo dedicato
                if (statoAttuale.getGiocatoreAttuale().isBot()) {
                    faiGiocareBot();
                }
            }
        }
    }
    
    /**
     * Apre la finestra per salvare la partita attuale su disco.
     */
    public void salvaPartita() {
        if (this.partita == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salva Sessione di JUno");
        
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Salvataggi JUno (*.JUno)", "JUno"));
        fileChooser.setSelectedFile(new java.io.File("Salvataggio.JUno"));
        
        if (fileChooser.showSaveDialog(this.view) == JFileChooser.APPROVE_OPTION) {
            String percorso = fileChooser.getSelectedFile().getAbsolutePath();
            
            // forza l'estensione ".JUno" se l'utente non l'ha scritta
            if (!percorso.endsWith(".JUno")) {
                percorso += ".JUno";
            }
            
            try {
                GestoreSalvataggi.Salva(partita, percorso);
                
                JOptionPane.showMessageDialog(this.view, 
                        "Partita salvata con successo", 
                        "Salvataggio completato", JOptionPane.INFORMATION_MESSAGE);
                        
                chiudiImpostazioni(); 
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this.view, 
                        "Errore durante il salvataggio:\n" + e.getMessage(), 
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Apre la finestra per caricare una partita e ripristina la grafica.
     */
    public void caricaPartita() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Carica Sessione di JUno");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Salvataggi JUno (*.JUno)", "JUno"));
        
        if (fileChooser.showOpenDialog(this.view) == JFileChooser.APPROVE_OPTION) {
            String percorso = fileChooser.getSelectedFile().getAbsolutePath();
            
            try {
                Partita partitaCaricata = GestoreSalvataggi.Carica(percorso);
                
                // puliscco la memoria prima di caricare qualsiasi cosa
                if (this.partita != null) {
                    this.partita.deleteObservers(); // scollega laS vecchia partita dalla view
                }
                if (this.timerBot != null && this.timerBot.isRunning()) {
                    this.timerBot.stop(); // blocca i bot della vecchia partita
                }
                
                // aggiorna i riferimenti
                this.partita = partitaCaricata;
                this.partita.addObserver(this); 
                
                // ripristina l'interfaccia grafica
                this.giocoInPausa = false;
                this.view.mostraTavolo();
                this.view.getPannelloTavolo().aggiorna(this.partita);
                
                JOptionPane.showMessageDialog(this.view, 
                        "Partita caricata con successo", 
                        "Caricamento completato", JOptionPane.INFORMATION_MESSAGE);
                
                if (this.partita.getGiocatoreAttuale().isBot()) {
                    faiGiocareBot();
                }
                
            } //controllo per i file manomessi o con estensioni diverse
            catch (java.io.StreamCorruptedException e) {
                JOptionPane.showMessageDialog(this.view, 
                        "Il file selezionato non è un salvataggio di JUno valido", 
                        "File non valido", JOptionPane.ERROR_MESSAGE);
            } 
            catch (Exception e) {
                JOptionPane.showMessageDialog(this.view, 
                        "Impossibile caricare il file.\nPotrebbe essere corrotto", 
                        "Errore di caricamento", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    /**
     * @return La lista delle carte attualmente selezionate per la giocata multipla (Number Rush).
     */
    public ArrayList<Carta> getCarteSelezionate() {return carteSelezionate;}
    
    
}