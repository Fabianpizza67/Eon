package com.Fabian.eon.listeners;

import com.Fabian.eon.systems.StationManager;
import com.Fabian.eon.movement.PilotSeat; // We will make this next!
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class CentralComputerListener implements Listener {

    private final String GUI_TITLE = ChatColor.DARK_AQUA + "Station Command";

    // PRIORITY HIGHEST: We speak last, so we override everyone else.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOpenComputer(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.BEACON) {

            // 1. BLOCK THE VANILLA INTERACTION
            event.setCancelled(true);

            // 2. Open our menu
            openStationMenu(event.getPlayer());
        }
    }

    // FALLBACK: If the beacon menu somehow opens anyway, slam it shut immediately.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVanillaBeaconOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.BEACON) {
            event.setCancelled(true);
            // Optional: Re-open our menu if it was a player
            if (event.getPlayer() instanceof Player) {
                openStationMenu((Player) event.getPlayer());
            }
        }
    }

    private void openStationMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, GUI_TITLE);

        inv.setItem(11, createItem(Material.COMPASS, "§bHelm Station", "§7Status: §fStandby", "§eClick to take controls"));
        inv.setItem(13, createItem(Material.SPYGLASS, "§cTactical Station", "§7Status: §fOffline", "§eClick to man weapons"));
        inv.setItem(15, createItem(Material.REDSTONE_BLOCK, "§6Engineering", "§7Status: §aOptimal", "§eClick to manage power"));

        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(GUI_TITLE)) return;
        e.setCancelled(true); // Don't steal the items!

        if (e.getCurrentItem() == null) return;

        Player p = (Player) e.getWhoClicked();
        Material mat = e.getCurrentItem().getType();
        p.closeInventory(); // Always close menu after selection

        if (mat == Material.COMPASS) {
            StationManager.setStation(p, "HELM");
            p.sendMessage("§b[Helm] §fInitializing Pilot Seat...");

            // SPAWN THE WASD SEAT
            PilotSeat.spawnSeat(p);

        } else if (mat == Material.SPYGLASS) {
            StationManager.setStation(p, "TACTICAL");
            p.sendMessage("§c[Tactical] §fWeapons online.");

        } else if (mat == Material.REDSTONE_BLOCK) {
            StationManager.setStation(p, "ENGINEERING");
            p.sendMessage("§6[Engineering] §fPower Grid Access Granted.");
        }
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}