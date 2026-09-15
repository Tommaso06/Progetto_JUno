package utility;

import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import model.Colori;
import model.Valori;

/**
 * Gestisce il caricamento e la memorizzazione di tutti gli asset grafici di JUno.
 * Utilizza il pattern Singleton per garantire un'unica istanza globale 
 * e impedire caricamenti multipli e sprechi di memoria.
 * Gli asset vengono letti tramite {@code getResourceAsStream}, garantendo 
 * la compatibilità cross-platform e il corretto funzionamento 
 * del gioco il gioco.
 */
public class GestoreAsset {
	
	private final String Path_Asset = "/Assets";
	private final String Path_Carte = Path_Asset + "/Mazzo";
	private final String Path_loghi = Path_Asset + "/Loghi";
	private final String Path_Retro_Carte = Path_Asset + "/RetroCarte";
	private final String Path_Tavoli = Path_Asset + "/Tavoli";
	
    private static GestoreAsset istanza;
    private HashMap<String, BufferedImage> immagini;
    
    private BufferedImage immaginiTavolo;
    private BufferedImage immagineCarta;
    private BufferedImage logo;
    private BufferedImage immagineRetroCarta;
    private BufferedImage immagineDeck;

    /**
     * Costruttore privato del GestoreAsset.
     * Impedisce l'istanziazione dall'esterno e avvia automaticamente 
     * il pre-caricamento in memoria di tutti gli asset principali
     * al momento della sua inztanziazione.
     */
    private GestoreAsset() {
        immagini = new HashMap<>();
        preCaricaLoghi();
        preCaricaCarte();
    }
    
    /**
     * Restituisce l'istanza globale e univoca di {@link GestoreAsset}.
     * Se l'istanza non esiste ancora, provvede a crearla.
     * @return L'istanza Singleton di GestoreAsset.
     */
    public static GestoreAsset getInstance() {
        if (istanza == null) {
            istanza = new GestoreAsset();
        }
        return istanza;
    }
    
    /**
     * Legge un'immagine dal percorso specificato nel Classpath e la salva
     * associandola a una chiave univoca. In caso di errore,
     * stampa un avviso nella console degli errori.
     * @param chiave, la chiave testuale con cui identificare l'immagine.
     * @param percorso, il percorso relativo all'interno del Classpath.
     */
    public void caricaImmagine(String chiave, String percorso) {
        try (java.io.InputStream tipo = getClass().getResourceAsStream(percorso)) {
            if (tipo != null) {
                BufferedImage img = ImageIO.read(tipo);
                immagini.put(chiave, img);
                System.out.println("Immagine caricata: " + chiave);
            } else { 
                System.err.println("Errore: Immagine non trovata: " + percorso);
            }
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento dell'immagine: " + percorso);
        }
    }

    /**
     * Recupera un'immagine precedentemente caricata e salvata nella mappa.
     * @param chiave, la chiave testuale univoca associata all'immagine.
     * @return L'oggetto {@link BufferedImage} corrispondente alla chiave, oppure {@code null} se non presente.
     */
    public BufferedImage getImmagine(String chiave) {
        return immagini.get(chiave);
    }
    
    /**
     * Esegue il pre-caricamento delle immagini relative all'interfaccia 
     * utente, come il logo principale del gioco e l'icona delle impostazioni.
     */
    public void preCaricaLoghi() {
        String percorsoLoghi = Path_loghi + "/JUNO.png";
        caricaImmagine("JUNO", percorsoLoghi);

        percorsoLoghi = Path_loghi + "/impostazioni.png";
        caricaImmagine("impostazioni", percorsoLoghi);
    }
    
    /**
     * Esegue il pre-caricamento di tutte le texture relative alle carte da gioco.
     * Itera automaticamente su tutti i colori e i valori definiti negli enum {@link Colori} e {@link Valori},
     * generando dinamicamente le chiavi e i percorsi.
     * Gestisce separatamente anche le carte jolly e il dorso delle carte.
     */
    public void preCaricaCarte() {
    	
    	caricaImmagine("back", Path_Retro_Carte + "/back.png");
        for(Colori colore : Colori.values()) {
            // salto i jolly durante il ciclo dei colori standard
            if (colore == Colori.JOLLY) {
                continue; 
            }
            for (Valori valore : Valori.values()) {
                // salto i valori speciali (li carico dopo)
                if (valore == Valori.WILD || valore == Valori.WILD_DRAW_FOUR) {
                    continue;
                }
                // creo una chiave univoca, es: "cinque-rosso" o "skip-blu"
                String chiave = valore.name().toLowerCase() + "-" + colore.name().toLowerCase();
                // costruisco il percorso del file, assumendo che i file siano .png
                String percorso = Path_Carte + "/" + chiave + ".png";              
                // carico e salvo nell'HashMap
                caricaImmagine(chiave, percorso);
            }
        }
        // gestione carte speciali
        Valori[] valoriSpeciali = {Valori.WILD, Valori.WILD_DRAW_FOUR};
        for (Valori valoreSpeciale : valoriSpeciali) {
            
            String chiave = valoreSpeciale.name().toLowerCase() + "-" + Colori.JOLLY.name().toLowerCase();
            String percorsoBase = Path_Carte + "/" + chiave + ".png";
            caricaImmagine(chiave, percorsoBase);
        }
    }
}