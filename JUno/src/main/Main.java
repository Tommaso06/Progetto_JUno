package main;
import javax.swing.SwingUtilities;
import controller.GameController;

/**
 * Classe principale dell'applicazione JUno.
 * Rappresenta il punto di avvio del gioco.
 * Si occupa esclusivamente di far partire il sistema delegando
 * l'inizializzazione della logica e dell'interfaccia al {@link GameController}.
 */
public class Main {

    /**
     * Metodo principale che avvia l'esecuzione del programma.
     * Utilizza {@link SwingUtilities#invokeLater(Runnable)} per assicurare che 
     * l'interfaccia grafica venga creata e gestita all'interno del 
     * Thread separato e dedicato di Swing.
     * Questo approccio è fondamentale per prevenire crash, rallentamenti visivi 
     * e lag delle animazioni.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameController();
        });
    }
}