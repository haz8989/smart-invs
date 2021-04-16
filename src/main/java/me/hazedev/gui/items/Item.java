package me.hazedev.gui.items;

import org.bukkit.inventory.ItemStack;

public class Item {

    private ItemStack item;

    public Item(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() { return item; }

    public void setItem(ItemStack item) {
        this.item = item;
    }

}
