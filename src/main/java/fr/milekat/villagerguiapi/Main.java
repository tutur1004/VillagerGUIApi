package fr.milekat.villagerguiapi;

import fr.milekat.villagerguiapi.event.ApiTestEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new ApiTestEvent(this), this);
    }
}
