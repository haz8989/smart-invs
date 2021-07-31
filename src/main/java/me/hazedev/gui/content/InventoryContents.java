package me.hazedev.gui.content;

import me.hazedev.gui.SmartInventory;
import me.hazedev.gui.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface InventoryContents {

    SmartInventory inventory();
    Pagination pagination();

    Optional<SlotIterator> iterator(String id);

    SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn);
    SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn);

    SlotIterator newIterator(String id, SlotIterator.Type type, SlotPos startPos);
    SlotIterator newIterator(SlotIterator.Type type, SlotPos startPos);

    Item[][] all();

    Optional<SlotPos> firstEmpty();

    Optional<Item> get(int row, int column);
    Optional<Item> get(SlotPos slotPos);

    InventoryContents set(int row, int column, Item item);
    InventoryContents set(SlotPos slotPos, Item item);

    InventoryContents add(Item item);

    InventoryContents fill(Item item);
    InventoryContents fillRow(int row, Item item);
    InventoryContents fillColumn(int column, Item item);
    InventoryContents fillBorders(Item item);

    InventoryContents fillRect(int fromRow, int fromColumn,
                               int toRow, int toColumn, Item item);
    InventoryContents fillRect(SlotPos fromPos, SlotPos toPos, Item item);

    <T> T property(String name);
    <T> T property(String name, T def);

    InventoryContents setProperty(String name, Object value);

    class Impl implements InventoryContents {

        private final SmartInventory inv;
        private final UUID player;

        private final Item[][] contents;

        private final Pagination pagination = new Pagination.Impl();
        private final Map<String, SlotIterator> iterators = new HashMap<>();
        private final Map<String, Object> properties = new HashMap<>();

        public Impl(SmartInventory inv, UUID player) {
            this.inv = inv;
            this.player = player;
            this.contents = new Item[inv.getRows()][inv.getColumns()];
        }

        @Override
        public SmartInventory inventory() { return inv; }

        @Override
        public Pagination pagination() { return pagination; }

        @Override
        public Optional<SlotIterator> iterator(String id) {
            return Optional.ofNullable(this.iterators.get(id));
        }

        @Override
        public SlotIterator newIterator(String id, SlotIterator.Type type, int startRow, int startColumn) {
            SlotIterator iterator = new SlotIterator.Impl(this, inv,
                    type, startRow, startColumn);

            this.iterators.put(id, iterator);
            return iterator;
        }

        @Override
        public SlotIterator newIterator(String id, SlotIterator.Type type, SlotPos startPos) {
            return newIterator(id, type, startPos.getRow(), startPos.getColumn());
        }

        @Override
        public SlotIterator newIterator(SlotIterator.Type type, int startRow, int startColumn) {
            return new SlotIterator.Impl(this, inv, type, startRow, startColumn);
        }

        @Override
        public SlotIterator newIterator(SlotIterator.Type type, SlotPos startPos) {
            return newIterator(type, startPos.getRow(), startPos.getColumn());
        }

        @Override
        public Item[][] all() { return contents; }

        @Override
        public Optional<SlotPos> firstEmpty() {
            for (int row = 0; row < contents.length; row++) {
                for(int column = 0; column < contents[0].length; column++) {
                    if(!this.get(row, column).isPresent())
                        return Optional.of(new SlotPos(row, column));
                }
            }

            return Optional.empty();
        }

        @Override
        public Optional<Item> get(int row, int column) {
            if(row >= contents.length)
                return Optional.empty();
            if(column >= contents[row].length)
                return Optional.empty();

            return Optional.ofNullable(contents[row][column]);
        }

        @Override
        public Optional<Item> get(SlotPos slotPos) {
            return get(slotPos.getRow(), slotPos.getColumn());
        }

        @Override
        public InventoryContents set(int row, int column, Item item) {
            if (row < contents.length && column < contents[row].length) {
                contents[row][column] = item;
                update(row, column, item != null ? item.getItem() : null);
            }
            return this;
        }

        @Override
        public InventoryContents set(SlotPos slotPos, Item item) {
            return set(slotPos.getRow(), slotPos.getColumn(), item);
        }

        @Override
        public InventoryContents add(Item item) {
            for(int row = 0; row < contents.length; row++) {
                for(int column = 0; column < contents[0].length; column++) {
                    if(contents[row][column] == null) {
                        set(row, column, item);
                        return this;
                    }
                }
            }

            return this;
        }

        @Override
        public InventoryContents fill(Item item) {
            for(int row = 0; row < contents.length; row++)
                for(int column = 0; column < contents[row].length; column++)
                    set(row, column, item);

            return this;
        }

        @Override
        public InventoryContents fillRow(int row, Item item) {
            if(row >= contents.length)
                return this;

            for(int column = 0; column < contents[row].length; column++)
                set(row, column, item);

            return this;
        }

        @Override
        public InventoryContents fillColumn(int column, Item item) {
            for(int row = 0; row < contents.length; row++)
                set(row, column, item);

            return this;
        }

        @Override
        public InventoryContents fillBorders(Item item) {
            fillRect(0, 0, inv.getRows() - 1, inv.getColumns() - 1, item);
            return this;
        }

        @Override
        public InventoryContents fillRect(int fromRow, int fromColumn, int toRow, int toColumn, Item item) {
            for(int row = fromRow; row <= toRow; row++) {
                for(int column = fromColumn; column <= toColumn; column++) {
                    if(row != fromRow && row != toRow && column != fromColumn && column != toColumn)
                        continue;

                    set(row, column, item);
                }
            }

            return this;
        }

        @Override
        public InventoryContents fillRect(SlotPos fromPos, SlotPos toPos, Item item) {
            return fillRect(fromPos.getRow(), fromPos.getColumn(), toPos.getRow(), toPos.getColumn(), item);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T property(String name) {
            return (T) properties.get(name);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T property(String name, T def) {
            return properties.containsKey(name) ? (T) properties.get(name) : def;
        }

        @Override
        public InventoryContents setProperty(String name, Object value) {
            properties.put(name, value);
            return this;
        }

        private void update(int row, int column, ItemStack item) {
            if(!inv.getManager().getOpenedPlayers(inv).contains(player))
                return;

            Inventory topInventory = Bukkit.getPlayer(player).getOpenInventory().getTopInventory();
            topInventory.setItem(inv.getColumns() * row + column, item);
        }

    }

}