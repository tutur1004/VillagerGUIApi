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

public class ApiTest implements Listener {
    private final Plugin plugin;
    public ApiTest(Plugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onNpcClick(NPCRightClickEvent event) {
        List<VillagerTrade> trades = new ArrayList<>();
        trades.add(new VillagerTrade(new ItemStack(Material.COBBLESTONE), new ItemStack(Material.EMERALD, 24), 1000));
        VillagerInventory tradeGui = new VillagerInventory(plugin, event.getClicker(), trades);
        tradeGui.setName(event.getNPC().getName());
        tradeGui.open();
    }

    @EventHandler
    public void onTrade(VillagerTradeCompleteEvent event) {
        event.getPlayer().sendMessage(event.getTrade().getItemOne().getType() + ":" + event.getTrade().getItemOne().getAmount() + ":" + event.getCount());
    }
}
