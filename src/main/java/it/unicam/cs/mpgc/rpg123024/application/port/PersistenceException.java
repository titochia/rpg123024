package it.unicam.cs.mpgc.rpg123024.application.port;

/**
 * Errore non verificato di persistenza (I/O, formato non valido, dati incoerenti).
 * <p>
 * <b>Responsabilità:</b> rappresentare i fallimenti dei {@link GameRepository} in modo indipendente
 * dalla tecnologia, così che il core e la GUI non conoscano {@code IOException} o eccezioni di
 * librerie specifiche.
 * <p>
 * <b>SOLID:</b> Dependency Inversion (il contratto della porta non espone dettagli degli adapter).
 */
public class PersistenceException extends RuntimeException {

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}