package it.unicam.cs.mpgc.rpg123024.gui.event;

import it.unicam.cs.mpgc.rpg123024.domain.event.GameEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameEventBusTest {

    private final GameEventBus bus = new GameEventBus();

    @Test
    void notifiesEverySubscriberInOrder() {
        List<String> received = new ArrayList<>();
        bus.subscribe(event -> received.add("first"));
        bus.subscribe(event -> received.add("second"));

        bus.publish(new GameEvent.GameWon());

        assertEquals(List.of("first", "second"), received);
    }

    @Test
    void unsubscribedConsumerIsNoLongerNotified() {
        List<GameEvent> received = new ArrayList<>();
        Consumer<GameEvent> subscriber = received::add;
        bus.subscribe(subscriber);

        bus.publish(new GameEvent.GameWon());
        bus.unsubscribe(subscriber);
        bus.publish(new GameEvent.GameOver());

        assertEquals(List.of(new GameEvent.GameWon()), received);
    }

    @Test
    void subscriberCanUnsubscribeWhileBeingNotified() {
        List<GameEvent> received = new ArrayList<>();
        Consumer<GameEvent> once = new Consumer<>() {
            @Override
            public void accept(GameEvent event) {
                received.add(event);
                bus.unsubscribe(this);
            }
        };
        bus.subscribe(once);

        bus.publish(new GameEvent.GameWon());
        bus.publish(new GameEvent.GameOver());

        assertEquals(1, received.size());
    }
}
