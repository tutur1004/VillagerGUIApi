package fr.milekat.villagerguiapi.api.villagergui.classes;

import fr.milekat.villagerguiapi.api.villagergui.ApiEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class VillagerInventory {
	private final Plugin plugin;
	private final Player player;
	private String name = "Villager shop";
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

	public void open() {
		Bukkit.getServer().getPluginManager().registerEvents(new ApiEvents(this), plugin);
	}
}
