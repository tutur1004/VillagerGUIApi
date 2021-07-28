package fr.milekat.villagerguiapi.api.villagergui.classes;

import fr.milekat.villagerguiapi.api.villagergui.ApiEvents;
import fr.milekat.villagerguiapi.api.villagergui.events.VillagerTradeCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class VillagerInventory {
	private final Plugin plugin;
	private final Player player;
	private String name = "";
	private List<VillagerTrade> trades;

	public VillagerInventory(Plugin plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}

	public VillagerInventory(Plugin plugin, Player player, List<VillagerTrade> trades) {
		this.plugin = plugin;
		this.player = player;
		this.trades = trades;
	}

	public VillagerInventory(Plugin plugin, Player player, String name, List<VillagerTrade> trades) {
		this.plugin = plugin;
		this.player = player;
		this.name = name;
		this.trades = trades;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public Player getPlayer() {
		return player;
	}

	public List<VillagerTrade> getTrades() {
		return trades;
	}

	public void setTrades(List<VillagerTrade> trades) {
		this.trades = trades;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Open the inventory, only work if the player has no inventory opened, call {@link VillagerInventory#close} first
	 */
	public void open() {
		if (player.getOpenInventory().getType().equals(InventoryType.CRAFTING)) {
			Bukkit.getServer().getPluginManager().registerEvents(new ApiEvents(this), plugin);
		}
	}

	/**
	 * <p><b>WARNING !</b> Don't call this method in an event {@link VillagerTradeCompleteEvent} or call it in async
	 * <br>If you call it sync, the event will be canceled !</p>
	 * <p><br>Example:
	 * <br>	Bukkit.getScheduler().runTask(plugin, ()-> <b>event.getInventory().close()</b>);</p>
	 */
	public void close() {
		player.closeInventory();
	}

	/**
	 * <p><b>WARNING !</b> Don't call this method in an event {@link VillagerTradeCompleteEvent} or call it in async
	 * <br>If you call it sync, the event will be canceled !</p>
	 * <p><br>Example:
	 * <br>	Bukkit.getScheduler().runTask(plugin, ()-> <b>event.getInventory().update()</b>);</p>
	 */
	public void update() {
		close();
		open();
	}
}
