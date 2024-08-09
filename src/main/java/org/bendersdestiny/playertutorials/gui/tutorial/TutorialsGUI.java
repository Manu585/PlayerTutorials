package org.bendersdestiny.playertutorials.gui.tutorial;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Setter
public class TutorialsGUI implements InventoryHolder {
    private Inventory inventory;
    @Getter
    private String inventoryTitle;
    @Getter
    private InventoryHolder holder;
    @Getter
    private int rows;
    @Getter
    private Map<Integer, ItemStack> items = new HashMap<>();

    public TutorialsGUI(String inventoryTitle, int rows, InventoryHolder holder) {
        this.inventoryTitle = inventoryTitle;
        this.rows = rows;
        this.holder = holder;

        this.createInventory();
    }

    /**
     * Creates the {@link Inventory}
     */
    private void createInventory() {
        inventory = Bukkit.createInventory(holder, rows * 9, inventoryTitle);

        for (int i : items.keySet()) {
            inventory.setItem(i, items.get(i));
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
