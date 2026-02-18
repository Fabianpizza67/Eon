package com.Fabian.eon;

import com.Fabian.eon.database.*;
import org.bukkit.plugin.java.JavaPlugin;
import com.Fabian.eon.listeners.CentralComputerListener;
import com.Fabian.eon.listeners.WeaponListener;
import com.Fabian.eon.movement.PilotSeat;
import org.bukkit.plugin.java.JavaPlugin;

public final class Eon extends JavaPlugin {

    private DatabaseManager dbManager;

    @Override
    public void onEnable() {
        // 1. Save Default Config
        saveDefaultConfig();

        // 2. Initialize Database
        String host = getConfig().getString("database.host");
        String port = getConfig().getString("database.port");
        String db   = getConfig().getString("database.name");
        String user = getConfig().getString("database.user");
        String pass = getConfig().getString("database.password");

        dbManager = new DatabaseManager();
        try {
            dbManager.connect(host, port, db, user, pass);
        } catch (Exception e) {
            // ADD THIS LINE:
            e.printStackTrace();

            getLogger().severe("Could not connect to MariaDB! Disabling Eon.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Register Commands
        getCommand("ship").setExecutor(new com.Fabian.eon.commands.ShipCommand());
        getLogger().info(">> Eon systems online. Ready for command, Admiral.");

        // This is where the magic happens! We register our listeners here.

        // 1. Register the Central Computer (The Beacon GUI)
        getServer().getPluginManager().registerEvents(new CentralComputerListener(), this);

        // 2. Register the Pilot Seat (The WASD Movement)
        getServer().getPluginManager().registerEvents(new PilotSeat(), this);

        // 3. Register the Tactical Station (The Spyglass Lasers)
        getServer().getPluginManager().registerEvents(new WeaponListener(), this);

        getLogger().info("Eon Systems Online. All stations reporting for duty.");

    }

    @Override
    public void onDisable() {
        if (dbManager != null) {
            dbManager.close();
        }
        getLogger().info(">> Eon shutting down.");
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }
}