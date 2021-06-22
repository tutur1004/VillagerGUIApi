package fr.milekat.villagerguiapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class McTools {
    /**
     * Check if the inventory can store all items stacks
     * @param size inventory size (Chest = 27, Player = 36, etc..)
     * @param count how much items you want add (How much stacks of ItemStack)
     */
    public static boolean canStore(Inventory baseInv, int size, ItemStack items, int count) {
        Inventory inv = Bukkit.createInventory(null, size, "canStore");
        inv.setContents(baseInv.getStorageContents());
        for (int i=0; i<count; i++) {
            final Map<Integer, ItemStack> map = inv.addItem(items); // Attempt to add in inventory
            if (!map.isEmpty()) return false; // If not empty, it means the player's inventory is full.
        }
        return true;
    }
}
