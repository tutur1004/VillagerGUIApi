package fr.milekat.villagerguiapi.event;

import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerInventory;
import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerTrade;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerInventoryCloseEvent;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerInventoryClickEvent;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerInventoryOpenEvent;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerTradeCompleteEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
/**
 * This class is only to test and show how to use my version of VillagerGuiAPI !
 * this example use Citizens plugin
 */
public class ApiTestEvent implements Listener {
    private final Plugin plugin;

    public ApiTestEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNpcClick(NPCRightClickEvent event) {
        List<VillagerTrade> trades = new ArrayList<>();
        if (event.getNPC().getId()==0) {
            trades.add(new VillagerTrade(new ItemStack(Material.COBBLESTONE), new ItemStack(Material.EMERALD, 24), 1000));
        }
        trades.add(new VillagerTrade(new ItemStack(Material.DIRT), new ItemStack(Material.EMERALD, 16), 10));
        VillagerInventory tradeGui = new VillagerInventory(plugin, event.getClicker(), trades);
        tradeGui.setName(event.getNPC().getName());
        tradeGui.open();
    }

    /**
     * Can be cancelled, Test it !
     */
    @EventHandler
    public void onTrade(VillagerTradeCompleteEvent event) {
        event.getPlayer().sendMessage("Amount of trades process: " + event.getCount());
        event.getPlayer().sendMessage("Item traded: " + event.getTrade().getItemOne().getType());
        event.getPlayer().sendMessage("Amount of first item: " + event.getTrade().getItemOne().getAmount());
        event.getPlayer().sendMessage("Amount of first item (Total): " + event.getTrade().getItemOne().getAmount() * event.getCount());
        event.getPlayer().sendMessage("Item received: " + event.getTrade().getResult().getType());
        event.getPlayer().sendMessage("Amount of received item for one trade: " + event.getTrade().getResult().getAmount());
        event.getPlayer().sendMessage("Amount of received item (Total): " + event.getTrade().getResult().getAmount() * event.getCount());
        event.getPlayer().sendMessage("Reaming uses: " + (event.getTrade().getMaxUses() - event.getCount()));
        /*
         * You can close the inventory after a trade, or whenever you want :)
         * don't forget to call it async or the event will be cancelled
         */
        if (event.getCount() > 5) Bukkit.getScheduler().runTask(plugin, ()-> event.getInventory().close());
        //event.setCancelled(true);
    }

    /**
     * Can be cancelled, Test it !
     */
    @EventHandler
    public void onTradeOpenGui(VillagerInventoryOpenEvent event) {
        event.getPlayer().sendMessage("Open");
        //event.setCancelled(true);
    }

    /**
     * Can be cancelled, Test it !
     */
    @EventHandler
    public void onTradeCloseGui(VillagerInventoryCloseEvent event) {
        event.getPlayer().sendMessage("Close");
        //event.setCancelled(true);
    }

    /**
     * Can be cancelled, Test it !
     */
    @EventHandler
    public void onTradeClickGui(VillagerInventoryClickEvent event) {
        event.getPlayer().sendMessage("Click");
        //event.setCancelled(true);
    }
}
