package it.unicam.cs.mpgc.rpg123024.application.usecase;

import it.unicam.cs.mpgc.rpg123024.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;

import java.util.Objects;
import java.util.Optional;

/**
 * Use case: caricamento dell'ultima partita salvata.
 * <p>
 * <b>Responsabilità:</b> ottenere dal {@link GameRepository} la sessione salvata, se presente.
 * <p>
 * <b>SOLID:</b> Single Responsibility; Dependency Inversion.
 */
public class LoadGame {

    private final GameRepository repository;

    public LoadGame(GameRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Optional<GameSession> execute() {
        return repository.load();
    }
}