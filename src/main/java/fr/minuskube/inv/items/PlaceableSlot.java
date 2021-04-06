package fr.minuskube.inv.items;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class PlaceableSlot extends ClickableItem {

    public PlaceableSlot(Consumer<InventoryClickEvent> consumer) {
        super(null, consumer);
    }

    public static PlaceableSlot empty() {
        return of(e -> {});
    }

    public static PlaceableSlot of(Consumer<InventoryClickEvent> consumer) {
        return new PlaceableSlot(consumer);
    }

    @Override
    public void run(InventoryClickEvent e) {
        InventoryAction a = e.getAction();
        if (a == InventoryAction.PLACE_ALL || a == InventoryAction.PLACE_SOME || a == InventoryAction.PLACE_ONE) {
            setItem(e.getCurrentItem());
            consumer.accept(e);
        } else {
            e.setCancelled(true);
        }
    }

}
