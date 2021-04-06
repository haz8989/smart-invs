package fr.minuskube.inv.items;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickableItem extends Item {

    protected Consumer<InventoryClickEvent> consumer;

    public ClickableItem(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        super(item);
        this.consumer = consumer;
    }

    public static ClickableItem empty(ItemStack item) {
        return of(item, e -> {});
    }

    public static ClickableItem of(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        return new ClickableItem(item, consumer);
    }

    public void run(InventoryClickEvent e) {
        consumer.accept(e);
        e.setCancelled(true);
    }

}
