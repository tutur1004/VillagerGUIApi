package fr.milekat.villagerguiapi.api.villagergui;

import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerInventory;
import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerTrade;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerInventoryCloseEvent;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerInventoryModifyEvent;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerInventoryOpenEvent;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerTradeCompleteEvent;
import fr.milekat.villagerguiapi.utils.McTools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class ApiEvents implements Listener {
    private final Merchant merchant;
    private final VillagerInventory inventory;

    public ApiEvents(VillagerInventory inventory) {
        this.inventory = inventory;
        Bukkit.getServer().getPluginManager().registerEvents(this, inventory.getPlugin());
        merchant = Bukkit.createMerchant(inventory.getName());
        merchant.setRecipes(this.toBukkitRecipes());
    }

    public void openFor(Player p) {
        p.openMerchant(merchant, true);
        VillagerInventoryOpenEvent event = new VillagerInventoryOpenEvent(inventory, p);
        Bukkit.getPluginManager().callEvent(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer().getUniqueId().equals(this.inventory.getPlayer().getUniqueId())) {
            VillagerInventoryCloseEvent closeEvent = new VillagerInventoryCloseEvent(inventory,
                    (Player) event.getPlayer());
            Bukkit.getPluginManager().callEvent(closeEvent);
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getAction() == InventoryAction.NOTHING) return;
        if (event.getWhoClicked().getUniqueId().equals(this.inventory.getPlayer().getUniqueId())) {
            VillagerInventoryModifyEvent modifyEvent = new VillagerInventoryModifyEvent(inventory,
                    (Player) event.getWhoClicked(), event.getCurrentItem());
            Bukkit.getPluginManager().callEvent(modifyEvent);
            if (event.getRawSlot() != 2) return;
            VillagerTrade trade = inventory.getTrades().get(((MerchantInventory)
                    event.getWhoClicked().getOpenInventory().getTopInventory()).getSelectedRecipeIndex());
            int tradesCount = 0;
            event.getWhoClicked().sendMessage(event.getAction().toString());
            if (event.getAction().equals(InventoryAction.PICKUP_ONE) ||
                    event.getAction().equals(InventoryAction.PICKUP_ALL) ||
                    event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                tradesCount = 1;
            } else if (event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
                // This situation is where the player HOT BAR Click the output item to buy multiple times at once.
                if (!event.getWhoClicked().getInventory().getItem(event.getHotbarButton()).getType().equals(Material.AIR)) {
                    tradesCount = canTrade(event, trade);
                }
            } else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                // This situation is where the player SHIFT+CLICKS the output item to buy multiple times at once.
                ItemStack result = event.getCurrentItem();
                if (result!=null) {
                    /* 3 - Get how many times the player can store the trade result in his inventory */
                    for (int i = 1; i <= canTrade(event, trade); i++) {
                        if (McTools.canStore(event.getWhoClicked().getInventory(), 36, result, i) && i <= trade.getMaxUses()) {
                            tradesCount = i;
                        } else break;
                    }
                }
            }
            if (tradesCount != 0 && !event.getCurrentItem().getType().equals(Material.AIR)) {
                Bukkit.getPluginManager().callEvent(
                        new VillagerTradeCompleteEvent(inventory, (Player) event.getWhoClicked(), trade, tradesCount));
            } else event.setCancelled(true);
        }
    }

    public List<MerchantRecipe> toBukkitRecipes() {
        List<MerchantRecipe> result = new ArrayList<>();
        for (VillagerTrade trd : this.inventory.getTrades()) {
            MerchantRecipe toAdd = new MerchantRecipe(trd.getResult(), trd.getMaxUses());
            toAdd.addIngredient(trd.getItemOne());
            if (trd.requiresTwoItems())
                toAdd.addIngredient(trd.getItemTwo());
            result.add(toAdd);
        }
        return result;
    }

    private int canTrade(InventoryClickEvent event, VillagerTrade trade) {
        ItemStack itemOne = event.getWhoClicked().getOpenInventory().getTopInventory().getItem(0);
        ItemStack itemTwo = event.getWhoClicked().getOpenInventory().getTopInventory().getItem(1);
        ItemStack result = event.getCurrentItem();
        if (result==null) return 0;
        int canTrade = 0;
        /* 1 - Get how many the player can trade with his items */
        if (itemOne != null) {
            canTrade = (int) Math.floor((double) itemOne.getAmount() / (double) trade.getItemOne().getAmount());
        }
        if (itemTwo != null) {
            canTrade = Math.min(canTrade,
                    (int) Math.floor((double) itemTwo.getAmount() / (double) trade.getItemTwo().getAmount()));
        }
        /* 2 - Check max stack size of result */
        return Math.min(canTrade, result.getMaxStackSize());
    }
}
