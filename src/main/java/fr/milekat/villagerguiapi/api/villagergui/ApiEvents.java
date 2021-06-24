package fr.milekat.villagerguiapi.api.villagergui;

import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerInventory;
import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerTrade;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerInventoryCloseEvent;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerInventoryModifyEvent;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerInventoryOpenEvent;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerTradeCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;

public class ApiEvents implements Listener {
    private final InventoryView merchantView;
    private final VillagerInventory inventory;

    public ApiEvents(VillagerInventory inventory) {
        this.inventory = inventory;
        //  Create Bukkit Merchant inventory
        Merchant merchant = Bukkit.createMerchant(inventory.getName());
        merchant.setRecipes(ApiUtils.toBukkitRecipes(inventory));
        //  Open inventory and trigger VillagerInventoryOpenEvent
        merchantView = inventory.getPlayer().openMerchant(merchant, true);
        Bukkit.getPluginManager().callEvent(new VillagerInventoryOpenEvent(inventory, inventory.getPlayer()));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer().getOpenInventory().equals(merchantView)) {
            Bukkit.getPluginManager().callEvent(new VillagerInventoryCloseEvent(inventory, (Player) event.getPlayer()));
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getAction()==InventoryAction.NOTHING || !event.getWhoClicked().getOpenInventory().equals(merchantView)) return;
        Bukkit.getPluginManager().callEvent(new VillagerInventoryModifyEvent(inventory, inventory.getPlayer(), event.getCurrentItem()));
        if (event.getRawSlot() != 2) return;
        //  Get the selected trade (Only work for Bukkit 1.9+)
        VillagerTrade trade = inventory.getTrades().get(((MerchantInventory) event.getWhoClicked().getOpenInventory().getTopInventory()).getSelectedRecipeIndex());
        int tradesCount = ApiUtils.getTradesCount(event, trade);
        if (tradesCount != 0) {
            Bukkit.getPluginManager().callEvent(new VillagerTradeCompleteEvent(inventory, inventory.getPlayer(), trade, tradesCount));
        } else event.setCancelled(true);
    }
}
