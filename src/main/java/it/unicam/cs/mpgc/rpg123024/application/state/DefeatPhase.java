package it.unicam.cs.mpgc.rpg123024.application.state;

/**
 * Fase terminale: l'eroe è stato sconfitto.
 * <p>
 * <b>Responsabilità:</b> identificare lo stato finale negativo; non abilita alcun comando.
 * <p>
 * <b>Pattern:</b> State concreto terminale. <b>SOLID:</b> Open/Closed.
 */
public final class DefeatPhase implements GamePhase {

    static final DefeatPhase INSTANCE = new DefeatPhase();

    private DefeatPhase() {
    }

    @Override
    public String id() {
        return "DEFEAT";
    }
}