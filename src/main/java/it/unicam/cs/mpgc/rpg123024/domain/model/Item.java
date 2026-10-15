package it.unicam.cs.mpgc.rpg123024.domain.model;

import java.util.Objects;

/**
 * Oggetto utilizzabile da un {@link Combatant} (es. pozioni).
 * <p>
 * <b>Responsabilità:</b> descrivere un oggetto (identificativo, nome, descrizione) e incapsulare
 * il suo effetto come {@link Effect} (pattern Strategy): nuovi oggetti si aggiungono fornendo un
 * nuovo effetto, senza modificare questa classe (Open/Closed).
 * <p>
 * <b>Architettura:</b> l'{@code id} è l'unico dato da persistere: in fase di caricamento
 * l'oggetto viene ricostruito da una factory, evitando di serializzare comportamento.
 *
 * @param id          identificativo univoco e stabile del tipo di oggetto
 * @param name        nome visualizzabile
 * @param description descrizione testuale
 * @param effect      effetto applicato all'uso
 */
public record Item(String id, String name, String description, Effect effect) {

    /** Comportamento di un oggetto: modifica il combattente bersaglio. */
    @FunctionalInterface
    public interface Effect {
        /** @param target combattente su cui applicare l'effetto */
        void applyTo(Combatant target);
    }

    public Item {
        requireNonBlank(id, "id");
        requireNonBlank(name, "name");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(effect, "effect");
    }

    /**
     * Applica l'effetto dell'oggetto al bersaglio.
     *
     * @param target combattente bersaglio
     */
    public void use(Combatant target) {
        Objects.requireNonNull(target, "target");
        effect.applyTo(target);
    }

    private static void requireNonBlank(String value, String field) {
        Objects.requireNonNull(value, field);
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}