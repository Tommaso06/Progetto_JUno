# JUno - Progetto Universitario

**Autori:**
* Tommaso Centra
* Giovanni DI Palma

## Specifiche del Progetto

### Versione di Java
Il progetto richiede come minimo **Java 14** per la compilazione, ma è stato sviluppato, ottimizzato e ampiamente testato su **Java 21**. Si consiglia l'esecuzione con il JDK 21 per garantire la massima compatibilità.

### Librerie e Architettura
L'interfaccia grafica è realizzata interamente in **Swing**. Il progetto si basa su una rigorosa architettura **MVC** (Model-View-Controller) e non richiede alcuna libreria esterna, utilizzando esclusivamente i pacchetti standard inclusi nel JDK.

### Classe Principale e Threading
La classe di avvio del programma è **`main.Main`** (situata in `src/main/Main.java`). 
Il metodo `main` si occupa esclusivamente di avviare il sistema, utilizzando `SwingUtilities.invokeLater()` per demandare l'inizializzazione del `GameController` e della GUI all'Event Dispatch Thread (EDT) di Swing. Questa scelta progettuale è fondamentale per mantenere le animazioni fluide e prevenire crash legati alla concorrenza grafica.

## Istruzioni di Gioco

### Avviare una Partita (Umani e Bot)
1. Importare l'archivio ZIP del progetto nell'IDE e assicurarsi che il Build Path sia impostato su Java 21.
2. Avviare l'esecuzione dalla classe `main.Main`.
3. Dal menu principale, cliccare su **Gioca**.
4. Selezionare la **Modalità Classica** o la **Modalità A Punti**.
5. Impostare il numero di giocatori umani e di bot.
6. (Opzionale) Attivare regole alternative come **Stacking** e/o **Number Rush**.
7. Premere **Avanti**, inserire i nomi dei giocatori e selezionare le strategie dei bot.
8. Premere **Avvia Partita**.

### Modalità Simulazione (Solo Bot)
È possibile osservare una partita giocata interamente dall'IA. Per avviarla, è sufficiente utilizzare l'apposito pulsante dedicato oppure portare a **0** il contatore dei giocatori umani: il sistema riconoscerà l'impostazione e avvierà il tavolo in totale autonomia.

## Regole Speciali e Varianti
* **Number Rush:** Quando questa opzione è attiva, l'interazione con le carte cambia. Mentre nella modalità classica la carta viene giocata istantaneamente al click, in Number Rush si sblocca un apposito pulsante "Gioca" per confermare l'azione, agevolando la riflessione sulle strategie.
* **Stacking:** Abilita l'accumulo delle penalità (carte +2 e +4). Quando lo Stacking è attivo, la meccanica di "Challenge" (Contesta) viene disabilitata per lasciare spazio agli accumulatori di penalità.

## Funzionalità Avanzate

### Salvataggio e Caricamento
Il gioco supporta la sospensione e la ripresa della partita. Essi sono gestiti internamente dall'interfaccia `Serializable`.
* **Salvataggio:** Accedendo alle impostazioni durante il gioco, è possibile serializzare su disco l'intera sessione.
* **Caricamento:** Dal menu principale o dalle impostazioni, selezionando un file di salvataggio si ripristina istantaneamente il tavolo e lo stato dei turni.

### Storico Mosse
Tutte le azioni, gli effetti e le dichiarazioni (o mancate dichiarazioni) di JUNO vengono tracciate. Durante la partita, lo storico è visibile in tempo reale all'interno di un pannello a scorrimento posizionato a destra del tasto "Contesta JUNO". Al termine del gioco, lo storico completo diventa consultabile in forma estesa all'interno della schermata della classifica.

## Pro del programma
* **Gestione "Hot-Seat":** Per garantire un'esperienza multiplayer locale leale, il sistema oscura automaticamente le carte in mano durante il cambio del turno, qualora ci siano più giocatori umani in partita, impedendo di sbirciare la mano altrui. Se gioca un solo umano contro i bot, le carte restano comodamente visibili.
* **Feedback Visivo:** L'interfaccia si arricchisce di un effetto hover dinamico sulle carte (ispirato a titoli come *Balatro*), rendendo l'interazione estremamente responsiva.
* **Ritmo di Gioco:** Per permettere al giocatore umano di seguire l'evoluzione della partita senza che l'IA giochi interi round in frazioni di secondo, i bot rispettano un'attesa artificiale calibrata (circa 4 secondi) per simulare un "tempo di riflessione" reale.

## Limitazioni
* **Cronologia Storico Mosse:** A causa della rapida esecuzione concatenata dei metodi e della complessità dei thread in background, i log stampati nello storico in-game potrebbero talvolta risultare visivamente accavallati o non strettamente in ordine cronologico al millisecondo. Tutte le mosse vengono comunque registrate.

## Licenza e Uso del Codice

Questo progetto universitario è open-source e rilasciato sotto i termini della **Licenza MIT** (vedi il file `LICENSE` per i dettagli).

Chiunque è libero di scaricare, studiare, modificare e utilizzare questo codice. Tuttavia, **è severamente vietato appropriarsi del progetto per spacciarlo come proprio**. 

Secondo i termini della licenza, chiunque utilizzi, copi o modifichi parti sostanziali di questo software è legalmente obbligato a:
* Mantenere intatto il file `LICENSE` originale all'interno del progetto.
* Riconoscere la paternità del codice lasciando i dovuti crediti agli autori originali (Tommaso Centra e Giovanni DI Palma).
