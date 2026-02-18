package com.Fabian.eon.systems;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StationManager {

    // Map: Player's UUID -> Role Name ("HELM", "TACTICAL", etc.)
    private static final Map<UUID, String> playerStations = new HashMap<>();

    public static void setStation(Player p, String station) {
        playerStations.put(p.getUniqueId(), station);
        p.sendTitle("§bStation Assigned", "§7Role: " + station, 10, 40, 10);
    }

    public static String getStation(Player p) {
        return playerStations.getOrDefault(p.getUniqueId(), "NONE");
    }

    // --- ADD THESE THREE METHODS ---

    public static boolean isPilot(Player p) {
        return getStation(p).equals("HELM");
    }

    public static boolean isGunner(Player p) {
        return getStation(p).equals("TACTICAL");
    }

    public static boolean isEngineer(Player p) {
        return getStation(p).equals("ENGINEERING");
    }
}