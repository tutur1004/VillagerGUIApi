package fr.milekat.villagerguiapi.api.villagergui.events;

import fr.milekat.villagerguiapi.api.villagergui.classes.VillagerInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VillagerInventoryOpenEvent extends Event implements Cancellable {
	private static final HandlerList HANDLERS = new HandlerList();
	private boolean CANCELLED;

	private final VillagerInventory inventory;
	private final Player player;

	public VillagerInventoryOpenEvent(VillagerInventory inventory, Player player) {
		super();
		this.inventory = inventory;
		this.player = player;
	}

	public VillagerInventory getInventory() {
		return inventory;
	}

	public Player getPlayer() {
		return player;
	}

	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return this.CANCELLED;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.CANCELLED = cancel;
	}
}
