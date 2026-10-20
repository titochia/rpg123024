package it.unicam.cs.mpgc.rpg123024.persistence.dto;

import it.unicam.cs.mpgc.rpg123024.application.port.PersistenceException;
import it.unicam.cs.mpgc.rpg123024.application.state.GameSession;
import it.unicam.cs.mpgc.rpg123024.domain.combat.MonsterAI;
import it.unicam.cs.mpgc.rpg123024.domain.factory.ItemCatalog;
import it.unicam.cs.mpgc.rpg123024.domain.map.Dungeon;
import it.unicam.cs.mpgc.rpg123024.domain.map.Position;
import it.unicam.cs.mpgc.rpg123024.domain.map.Room;
import it.unicam.cs.mpgc.rpg123024.domain.model.Hero;
import it.unicam.cs.mpgc.rpg123024.domain.model.Item;
import it.unicam.cs.mpgc.rpg123024.domain.model.Monster;
import it.unicam.cs.mpgc.rpg123024.domain.model.Stats;
import it.unicam.cs.mpgc.rpg123024.persistence.dto.GameSnapshot.DungeonDto;
import it.unicam.cs.mpgc.rpg123024.persistence.dto.GameSnapshot.HeroDto;
import it.unicam.cs.mpgc.rpg123024.persistence.dto.GameSnapshot.MonsterDto;
import it.unicam.cs.mpgc.rpg123024.persistence.dto.GameSnapshot.PositionDto;
import it.unicam.cs.mpgc.rpg123024.persistence.dto.GameSnapshot.RoomDto;
import it.unicam.cs.mpgc.rpg123024.persistence.dto.GameSnapshot.StatsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Convertitore bidirezionale tra {@link GameSession} (dominio) e {@link GameSnapshot} (DTO).
 * <p>
 * <b>Responsabilità:</b> estrarre lo stato da salvare e ricostruire sessione, eroe, dungeon e
 * oggetti dal DTO, risolvendo gli {@code id} tramite {@link ItemCatalog} e reiniettando
 * l'AI dei mostri (configurazione, non dato). Verifica la versione del formato.
 * <p>
 * <b>SOLID:</b> Single Responsibility (solo mappatura, indipendente dal formato file e da Jackson);
 * Dependency Inversion (usa {@code ItemCatalog} e {@code MonsterAI} astratti). La mappatura non
 * dipende dal tipo di file: lo stesso mapper servirebbe per SQLite o rete.
 */
public class SnapshotMapper {

    static final int VERSION = 1;

    private final ItemCatalog catalog;
    private final MonsterAI monsterAI;

    public SnapshotMapper(ItemCatalog catalog, MonsterAI monsterAI) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
        this.monsterAI = Objects.requireNonNull(monsterAI, "monsterAI");
    }

    public GameSnapshot toSnapshot(GameSession session) {
        Hero hero = session.hero();
        HeroDto heroDto = new HeroDto(hero.name(), toDto(hero.stats()), hero.currentHealth(),
                hero.level(), hero.experience(), hero.inventory().capacity(),
                hero.inventory().items().stream().map(Item::id).toList());

        Dungeon dungeon = session.dungeon();
        List<RoomDto> rooms = new ArrayList<>();
        for (int r = 0; r < dungeon.rows(); r++) {
            for (int c = 0; c < dungeon.cols(); c++) {
                Room room = dungeon.room(new Position(r, c));
                rooms.add(new RoomDto(r, c, room.isVisited(),
                        room.monster().map(this::toDto).orElse(null),
                        room.loot().stream().map(Item::id).toList()));
            }
        }
        DungeonDto dungeonDto = new DungeonDto(dungeon.rows(), dungeon.cols(),
                toDto(dungeon.start()), toDto(dungeon.exit()), rooms);
        return new GameSnapshot(VERSION, heroDto, dungeonDto, toDto(session.position()));
    }

    public GameSession toSession(GameSnapshot snapshot) {
        if (snapshot.version() != VERSION) {
            throw new PersistenceException("unsupported save version: " + snapshot.version());
        }
        HeroDto heroDto = snapshot.hero();
        Hero hero = new Hero(heroDto.name(), toStats(heroDto.baseStats()), heroDto.inventoryCapacity(),
                heroDto.level(), heroDto.experience(), heroDto.currentHealth());
        heroDto.items().forEach(id -> hero.inventory().add(toItem(id)));

        DungeonDto dungeonDto = snapshot.dungeon();
        Dungeon dungeon = new Dungeon(dungeonDto.rows(), dungeonDto.cols(),
                toPosition(dungeonDto.start()), toPosition(dungeonDto.exit()));
        for (RoomDto roomDto : dungeonDto.rooms()) {
            Room room = dungeon.room(new Position(roomDto.row(), roomDto.col()));
            if (roomDto.visited()) {
                room.markVisited();
            }
            if (roomDto.monster() != null) {
                room.placeMonster(toMonster(roomDto.monster()));
            }
            roomDto.loot().forEach(id -> room.addLoot(toItem(id)));
        }
        return new GameSession(hero, dungeon, monsterAI, toPosition(snapshot.position()));
    }

    private Item toItem(String id) {
        return catalog.findById(id).orElseThrow(() -> new PersistenceException("unknown item id: " + id));
    }

    private Monster toMonster(MonsterDto dto) {
        return new Monster(dto.name(), toStats(dto.stats()), dto.experienceReward(), dto.currentHealth());
    }

    private MonsterDto toDto(Monster monster) {
        return new MonsterDto(monster.name(), toDto(monster.stats()),
                monster.currentHealth(), monster.experienceReward());
    }

    private static StatsDto toDto(Stats stats) {
        return new StatsDto(stats.maxHealth(), stats.attack(), stats.defense(), stats.speed());
    }

    private static Stats toStats(StatsDto dto) {
        return new Stats(dto.maxHealth(), dto.attack(), dto.defense(), dto.speed());
    }

    private static PositionDto toDto(Position position) {
        return new PositionDto(position.row(), position.col());
    }

    private static Position toPosition(PositionDto dto) {
        return new Position(dto.row(), dto.col());
    }
}