package it.unicam.cs.mpgc.rpg123024.gui.presenter;

import it.unicam.cs.mpgc.rpg123024.gui.view.CombatView;
import it.unicam.cs.mpgc.rpg123024.gui.view.CombatantStatus;
import it.unicam.cs.mpgc.rpg123024.gui.view.ExplorationView;
import it.unicam.cs.mpgc.rpg123024.gui.view.HeroStatus;
import it.unicam.cs.mpgc.rpg123024.gui.view.InventoryView;
import it.unicam.cs.mpgc.rpg123024.gui.view.ItemEntry;
import it.unicam.cs.mpgc.rpg123024.gui.view.MapCell;
import it.unicam.cs.mpgc.rpg123024.gui.view.MenuView;

import java.util.ArrayList;
import java.util.List;

final class Fakes {

    private Fakes() {
    }

    static class FakeNavigator implements Navigator {
        final List<String> screens = new ArrayList<>();

        @Override
        public void showMenu() {
            screens.add("MENU");
        }

        @Override
        public void showExploration() {
            screens.add("EXPLORATION");
        }

        @Override
        public void showCombat() {
            screens.add("COMBAT");
        }

        @Override
        public void showInventory() {
            screens.add("INVENTORY");
        }

        @Override
        public void showGameEnd(boolean victory) {
            screens.add(victory ? "VICTORY" : "DEFEAT");
        }

        String last() {
            return screens.isEmpty() ? null : screens.get(screens.size() - 1);
        }
    }

    static class FakeMenuView implements MenuView {
        final List<String> errors = new ArrayList<>();
        final List<String> messages = new ArrayList<>();

        @Override
        public void showError(String message) {
            errors.add(message);
        }

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }
    }

    static class FakeExplorationView implements ExplorationView {
        final List<String> errors = new ArrayList<>();
        final List<String> messages = new ArrayList<>();
        int rows;
        int cols;
        List<MapCell> cells = List.of();
        HeroStatus hero;
        List<String> log = List.of();

        @Override
        public void showError(String message) {
            errors.add(message);
        }

        @Override
        public void showMap(int rows, int cols, List<MapCell> cells) {
            this.rows = rows;
            this.cols = cols;
            this.cells = cells;
        }

        @Override
        public void showHero(HeroStatus hero) {
            this.hero = hero;
        }

        @Override
        public void showLog(List<String> messages) {
            this.log = List.copyOf(messages);
        }

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        MapCell cell(int row, int col) {
            return cells.stream().filter(c -> c.row() == row && c.col() == col).findFirst().orElseThrow();
        }
    }

    static class FakeCombatView implements CombatView {
        final List<String> errors = new ArrayList<>();
        CombatantStatus hero;
        CombatantStatus monster;
        List<String> actions = List.of();
        List<String> log = List.of();

        @Override
        public void showError(String message) {
            errors.add(message);
        }

        @Override
        public void showCombatants(CombatantStatus hero, CombatantStatus monster) {
            this.hero = hero;
            this.monster = monster;
        }

        @Override
        public void showActions(List<String> actionNames) {
            this.actions = actionNames;
        }

        @Override
        public void showLog(List<String> messages) {
            this.log = List.copyOf(messages);
        }
    }

    static class FakeInventoryView implements InventoryView {
        final List<String> errors = new ArrayList<>();
        List<ItemEntry> items = List.of();
        int capacity;
        HeroStatus hero;

        @Override
        public void showError(String message) {
            errors.add(message);
        }

        @Override
        public void showItems(List<ItemEntry> items, int capacity) {
            this.items = items;
            this.capacity = capacity;
        }

        @Override
        public void showHero(HeroStatus hero) {
            this.hero = hero;
        }
    }
}
