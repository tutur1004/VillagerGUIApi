package fr.milekat.villagerguiapi.api.villagergui;

import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerInventory;
import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerTrade;
import fr.milekat.villagerguiapi.utils.McTools;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class ApiUtils {
    public static int canTrade(InventoryClickEvent event, VillagerTrade trade) {
        ItemStack itemOne = event.getWhoClicked().getOpenInventory().getTopInventory().getItem(0);
        ItemStack itemTwo = event.getWhoClicked().getOpenInventory().getTopInventory().getItem(1);
        ItemStack result = event.getCurrentItem();
        if (result==null) return 0;
        if (trade.getItemOne() == null && itemOne != null && itemTwo == null) {
            itemTwo = itemOne;
            itemOne = null;
        }
        if (trade.getItemTwo() == null && itemOne == null && itemTwo != null) {
            itemOne = itemTwo;
            itemTwo = null;
        }
        int canTrade = 0;
        /* 1 - Get how many the player can trade with his items */
        if (itemOne != null) {
            canTrade = (int) Math.floor((double) itemOne.getAmount() / (double) trade.getItemOne().getAmount());
        }
        if (itemTwo != null) {
            canTrade = Math.min(canTrade, (int) Math.floor((double) itemTwo.getAmount() / (double) trade.getItemTwo().getAmount()));
        }
        /* 2 - Check max stack size of result */
        return Math.min(canTrade, result.getMaxStackSize());
    }

    public static int getTradesCount(InventoryClickEvent event, VillagerTrade trade) {
        int tradesCount = 0;
        if (event.getAction().equals(InventoryAction.PICKUP_ONE) ||
                event.getAction().equals(InventoryAction.PICKUP_ALL) ||
                event.getAction().equals(InventoryAction.PICKUP_HALF)) {
            //  This situation is where the player collect only one result stack.
            tradesCount = 1;
        } else if (event.getAction().equals(InventoryAction.HOTBAR_SWAP)) {
            // This situation is where the player collect to his hot bar a full result stack.
            if (event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) == null) {
                tradesCount = 1;
            }
        } else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
            // This situation is where the player SHIFT+CLICKS the output item to full trade.
            ItemStack result = event.getCurrentItem();
            if (result!=null) {
                /* 3 - Get how many times the player can store the trade result in his inventory */
                for (int i = 1; i <= ApiUtils.canTrade(event, trade); i++) {
                    if (McTools.canStore(event.getWhoClicked().getInventory(), 36, result, i) && i <= trade.getMaxUses()) {
                        tradesCount = i;
                    } else break;
                }
            }
        }
        return tradesCount;
    }

    public static List<MerchantRecipe> toBukkitRecipes(VillagerInventory inventory) {
        List<MerchantRecipe> result = new ArrayList<>();
        for (VillagerTrade trade : inventory.getTrades()) {
            MerchantRecipe merchant = new MerchantRecipe(trade.getResult(), trade.getMaxUses());
            merchant.addIngredient(trade.getItemOne());
            if (trade.requiresTwoItems()) merchant.addIngredient(trade.getItemTwo());
            result.add(merchant);
        }
        return result;
    }
}
