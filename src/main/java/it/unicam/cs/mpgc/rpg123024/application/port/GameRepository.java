package it.unicam.cs.mpgc.rpg123024.application.port;

import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;

import java.util.Optional;

/**
 * Porta in uscita per la persistenza della partita.
 * <p>
 * <b>Responsabilità:</b> definire il contratto di salvataggio e caricamento di una
 * {@link GameSession}, indipendente dal supporto (JSON, SQLite, rete).
 * <p>
 * <b>Pattern:</b> Repository / Ports &amp; Adapters. <b>SOLID:</b> Dependency Inversion (il core
 * non conosce il formato di persistenza); Open/Closed (nuovi supporti = nuovi adapter, nessuna
 * modifica al core).
 */
public interface GameRepository {

    void save(GameSession session);

    Optional<GameSession> load();
}