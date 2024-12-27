package fr.pmk.lucksecure.paper;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    @Override
    public void onEnable() {
        try {
            new PaperLuckSecure(this).init();
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "LuckSecure init error", e);
            this.onDisable();
        }
    }

}
