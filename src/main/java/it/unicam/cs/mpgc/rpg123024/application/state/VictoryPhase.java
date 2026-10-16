package it.unicam.cs.mpgc.rpg123024.application.state;

/**
 * Fase terminale: la partita è stata vinta.
 * <p>
 * <b>Responsabilità:</b> identificare lo stato finale positivo; non abilita alcun comando (tutti
 * ereditano il rifiuto di {@link GamePhase}).
 * <p>
 * <b>Pattern:</b> State concreto terminale. <b>SOLID:</b> Open/Closed (aggiunta senza modificare
 * le altre fasi).
 */
public final class VictoryPhase implements GamePhase {

    static final VictoryPhase INSTANCE = new VictoryPhase();

    private VictoryPhase() {
    }

    @Override
    public String id() {
        return "VICTORY";
    }
}