package it.unicam.cs.mpgc.rpg123024.application.usecase;

import it.unicam.cs.mpgc.rpg123024.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;

import java.util.Objects;

/**
 * Use case: salvataggio della partita corrente.
 * <p>
 * <b>Responsabilità:</b> inoltrare la sessione al {@link GameRepository}.
 * <p>
 * <b>SOLID:</b> Single Responsibility; Dependency Inversion (indipendente dal formato di
 * persistenza).
 */
public class SaveGame {

    private final GameRepository repository;

    public SaveGame(GameRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public void execute(GameSession session) {
        Objects.requireNonNull(session, "session");
        if (!session.phase().allowsSaving()) {
            throw new IllegalStateException("saving not allowed in phase " + session.phase().id());
        }
        repository.save(session);
    }
}