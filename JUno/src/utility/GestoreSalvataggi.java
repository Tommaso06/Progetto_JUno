package utility;

import model.Partita;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * gestisce l'esportazione e l'importazione dello stato di gioco.
 * Sfrutta le librerie moderne NIO.2 di Java.
 */
public final class GestoreSalvataggi {

    private GestoreSalvataggi() {}

    /**
     * serializza l'intera sessione di gioco (Partita e Storico incluso) su disco.
     */
    public static void Salva(Partita partita, String percorsoAssoluto) throws Exception {
        Path percorso = Paths.get(percorsoAssoluto);
        
        // uso un try-with-resources per massimizzare l'efficienza
        try (OutputStream fileOut = Files.newOutputStream(percorso);
             BufferedOutputStream bufferOut = new BufferedOutputStream(fileOut);
             ObjectOutputStream objectOut = new ObjectOutputStream(bufferOut)) {
             objectOut.writeObject(partita);
        }
    }

    /**
     * deserializza il file e ripristina la sessione di gioco.
     */
    public static Partita Carica(String percorsoAssoluto) throws Exception {
        Path percorso = Paths.get(percorsoAssoluto);
        
        try (InputStream fileIn = Files.newInputStream(percorso);
             BufferedInputStream bufferIn = new BufferedInputStream(fileIn);
             ObjectInputStream objectIn = new ObjectInputStream(bufferIn)) {
             return (Partita) objectIn.readObject();
        }
    }
}