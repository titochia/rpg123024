package it.unicam.cs.mpgc.rpg123024.persistence.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unicam.cs.mpgc.rpg123024.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg123024.application.port.PersistenceException;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.persistence.dto.GameSnapshot;
import it.unicam.cs.mpgc.rpg123024.persistence.dto.SnapshotMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

/**
 * Adapter di {@link GameRepository} che salva la partita come file JSON.
 * <p>
 * <b>Responsabilità:</b> serializzare/deserializzare lo {@link GameSnapshot} su un file (scrittura su
 * file temporaneo e sostituzione, per non corrompere il salvataggio precedente in caso di errore) e
 * tradurre ogni errore tecnico in {@link PersistenceException}. La conversione dominio-DTO è delegata a
 * {@link SnapshotMapper}.
 * <p>
 * <b>Pattern:</b> Repository / Adapter (Ports &amp; Adapters). <b>SOLID:</b> Single Responsibility
 * (solo I/O e formato JSON); Liskov e Open/Closed (sostituibile con SQLite o rete senza modificare
 * core o GUI, essendo iniettato nella composition root).
 */
public class JsonGameRepository implements GameRepository {

    private final Path file;
    private final SnapshotMapper mapper;
    private final ObjectMapper json = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public JsonGameRepository(Path file, SnapshotMapper mapper) {
        this.file = Objects.requireNonNull(file, "file");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    @Override
    public void save(GameSession session) {
        GameSnapshot snapshot = mapper.toSnapshot(session);
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            json.writeValue(temp.toFile(), snapshot);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new PersistenceException("cannot save game to " + file, e);
        }
    }

    @Override
    public Optional<GameSession> load() {
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            return Optional.of(mapper.toSession(json.readValue(file.toFile(), GameSnapshot.class)));
        } catch (IOException | IllegalArgumentException | IllegalStateException e) {
            throw new PersistenceException("cannot load game from " + file, e);
        }
    }
}