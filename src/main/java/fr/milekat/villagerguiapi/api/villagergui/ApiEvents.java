package fr.milekat.villagerguiapi.api.villagergui;

import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerInventory;
import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerTrade;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerInventoryCloseEvent;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerInventoryClickEvent;
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
        VillagerInventoryClickEvent inventoryClickEvent = new VillagerInventoryClickEvent(inventory, inventory.getPlayer(), event.getCurrentItem());
        Bukkit.getPluginManager().callEvent(inventoryClickEvent);
        if (inventoryClickEvent.isCancelled()) event.setCancelled(true);
        if (event.getRawSlot() != 2) return;
        if (MINECRAFT_VERSION < 9) {//  MerchantInventory#getSelectedRecipeIndex, introduce in Bukkit 1.9
            Bukkit.getPluginManager().callEvent(new VillagerTradeCompleteEvent(inventory, inventory.getPlayer(), null, 0));
            return;
        }
        VillagerTrade trade = inventory.getTrades().get(((MerchantInventory) event.getWhoClicked().getOpenInventory().getTopInventory()).getSelectedRecipeIndex());
        // TODO: 25/06/2021 Manually proceed trade(s) to prevent Minecraft bug with full inventory
        int tradesCount = ApiUtils.getTradesCount(event, trade);
        if (tradesCount != 0) {
            VillagerTradeCompleteEvent tradeCompleteEvent = new VillagerTradeCompleteEvent(inventory, inventory.getPlayer(), trade, tradesCount);
            Bukkit.getPluginManager().callEvent(tradeCompleteEvent);
            if (tradeCompleteEvent.isCancelled()) event.setCancelled(true);
            else trade.setMaxUses(trade.getMaxUses() - tradesCount);
        } else event.setCancelled(true);
    }

    /**
     * Sniped to get MINECRAFT_VERSION, needed to verify if plugin is running in lower version than Bukkit 1.9 !
     */
    public static final int MINECRAFT_VERSION;
    static {
        final String serverPath = Bukkit.getServer().getClass().getPackage().getName();
        final String packageVersion = serverPath.substring(serverPath.lastIndexOf(".") + 2);
        MINECRAFT_VERSION = Integer.parseInt(packageVersion.substring(0, packageVersion.lastIndexOf("_")).replace("_", ".").substring(2));
    }
}
