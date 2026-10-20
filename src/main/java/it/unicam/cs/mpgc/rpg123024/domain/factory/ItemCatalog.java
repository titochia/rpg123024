package it.unicam.cs.mpgc.rpg123024.domain.factory;

import it.unicam.cs.mpgc.rpg123024.domain.model.Item;

import java.util.Optional;

/**
 * Catalogo che ricostruisce un {@link Item} a partire dal suo identificativo stabile.
 * <p>
 * <b>Responsabilità:</b> risolvere gli {@code id} in oggetti con il relativo comportamento, così che
 * la persistenza salvi solo dati (gli {@code id}) e mai codice o lambda.
 * <p>
 * <b>SOLID:</b> Dependency Inversion (persistenza e Domain dipendono da questa astrazione);
 * Open/Closed (nuovi cataloghi, ad esempio da file, senza modifiche ai chiamanti); Interface
 * Segregation (un solo metodo).
 */
public interface ItemCatalog {

    Optional<Item> findById(String id);
}