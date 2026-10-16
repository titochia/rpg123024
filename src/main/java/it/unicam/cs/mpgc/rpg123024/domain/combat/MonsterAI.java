package it.unicam.cs.mpgc.rpg123024.domain.combat;

/**
 * Strategia di scelta dell'azione per un mostro.
 * <p>
 * <b>Responsabilità:</b> dato lo stato dei due {@link Fighter}, decidere la {@link TurnAction} del
 * turno del mostro.
 * <p>
 * <b>Pattern:</b> Strategy. <b>SOLID:</b> Open/Closed (nuovi comportamenti, ad esempio
 * prudente o casuale, senza toccare il motore); Dependency Inversion (il motore dipende
 * dall'astrazione); Interface Segregation (un solo metodo, interfaccia funzionale).
 */
@FunctionalInterface
public interface MonsterAI {

    TurnAction chooseAction(Fighter self, Fighter opponent);
}