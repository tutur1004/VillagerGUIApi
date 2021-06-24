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
    private final VillagerInventory inventory;
    private final Merchant merchant;
    private InventoryView merchantView;

    public ApiEvents(VillagerInventory inventory) {
        this.inventory = inventory;
        //  Create Bukkit Merchant inventory
        merchant = Bukkit.createMerchant(inventory.getName());
        merchant.setRecipes(ApiUtils.toBukkitRecipes(inventory));
        //  Open inventory and trigger VillagerInventoryOpenEvent
        merchantView = inventory.getPlayer().openMerchant(merchant, true);
        VillagerInventoryOpenEvent inventoryOpenEvent = new VillagerInventoryOpenEvent(inventory, inventory.getPlayer());
        Bukkit.getPluginManager().callEvent(inventoryOpenEvent);
        if (inventoryOpenEvent.isCancelled()) {
            inventory.getPlayer().closeInventory();
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getPlayer().getOpenInventory().equals(merchantView)) return;
        VillagerInventoryCloseEvent inventoryCloseEvent = new VillagerInventoryCloseEvent(inventory, (Player) event.getPlayer());
        Bukkit.getPluginManager().callEvent(inventoryCloseEvent);
        if (inventoryCloseEvent.isCancelled()) Bukkit.getScheduler().runTask(inventory.getPlugin(), ()-> merchantView = inventory.getPlayer().openMerchant(merchant, true));
        else HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getAction()==InventoryAction.NOTHING || !event.getWhoClicked().getOpenInventory().equals(merchantView)) return;
        VillagerInventoryModifyEvent inventoryModifyEvent = new VillagerInventoryModifyEvent(inventory, inventory.getPlayer(), event.getCurrentItem());
        Bukkit.getPluginManager().callEvent(inventoryModifyEvent);
        if (inventoryModifyEvent.isCancelled()) event.setCancelled(true);
        if (event.getRawSlot() != 2) return;
        //  Get the selected trade (Only work for Bukkit 1.9+)
        VillagerTrade trade = inventory.getTrades().get(((MerchantInventory) event.getWhoClicked().getOpenInventory().getTopInventory()).getSelectedRecipeIndex());
        int tradesCount = ApiUtils.getTradesCount(event, trade);
        if (tradesCount != 0) {
            VillagerTradeCompleteEvent tradeCompleteEvent = new VillagerTradeCompleteEvent(inventory, inventory.getPlayer(), trade, tradesCount);
            Bukkit.getPluginManager().callEvent(tradeCompleteEvent);
            if (tradeCompleteEvent.isCancelled()) event.setCancelled(true);
        } else event.setCancelled(true);
    }
}
