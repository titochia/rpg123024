package it.unicam.cs.mpgc.rpg123024.domain.combat;

/**
 * Value object immutabile che descrive l'esito di un'azione di combattimento.
 * <p>
 * <b>Responsabilità:</b> trasportare, in forma neutra rispetto alla UI, chi ha agito, con quale
 * azione, su chi e con quale entità numerica (danno, cura, o 0 per azioni senza valore numerico).
 * <p>
 * <b>Architettura:</b> è il dato che i layer esterni (Presenter, log, futuri client web/mobile)
 * traducono in messaggi, senza che il Domain conosca la presentazione.
 *
 * @param actorName  nome di chi ha eseguito l'azione
 * @param actionName nome dell'azione
 * @param targetName nome del destinatario
 * @param amount     entità numerica prodotta dall'azione
 */
public record ActionResult(String actorName, String actionName, String targetName, int amount) {
}