package fr.milekat.villagerguiapi.event;

import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerInventory;
import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerTrade;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerTradeCompleteEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
/**
 * This class is only to test and show how to use my version of VillagerGuiAPI !
 * this example work with Citizens plugin
 */
public record ApiTestEvent(Plugin plugin) implements Listener {
    @EventHandler
    public void onNpcClick(NPCRightClickEvent event) {
        List<VillagerTrade> trades = new ArrayList<>();
        if (event.getNPC().getId()==0) {
            trades.add(new VillagerTrade(new ItemStack(Material.COBBLESTONE), new ItemStack(Material.EMERALD, 24), 1000));
        }
        trades.add(new VillagerTrade(new ItemStack(Material.DIRT), new ItemStack(Material.EMERALD, 16), 1000));
        VillagerInventory tradeGui = new VillagerInventory(plugin, event.getClicker(), trades);
        tradeGui.setName(event.getNPC().getName());
        tradeGui.open();
    }

    @EventHandler
    public void onTrade(VillagerTradeCompleteEvent event) {
        event.getPlayer().sendMessage(event.getTrade().getItemOne().getType() + ":" + event.getTrade().getItemOne().getAmount() + ":" + event.getCount());
    }
}
