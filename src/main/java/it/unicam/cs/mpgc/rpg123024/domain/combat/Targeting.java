package it.unicam.cs.mpgc.rpg123024.domain.combat;

/**
 * Destinatario di un'azione di combattimento rispetto a chi la esegue.
 * <p>
 * <b>Responsabilità:</b> distinguere le azioni rivolte a sé (cure, scudi) da quelle rivolte
 * all'avversario (attacchi, veleni), così che una sola classe di azione copra entrambi i casi.
 * <p>
 * <b>SOLID:</b> Single Responsibility; evita la proliferazione di sottoclassi di azione (DRY).
 */
public enum Targeting {
    SELF,
    OPPONENT
}