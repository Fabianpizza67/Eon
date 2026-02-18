package com.Fabian.eon.listeners;

import com.Fabian.eon.systems.StationManager;
import com.Fabian.eon.utils.ShipScanner;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class WeaponListener implements Listener {

    @EventHandler
    public void onFireWeapon(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getInventory().getItemInMainHand().getType() != Material.SPYGLASS) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (!StationManager.getStation(player).equals("TACTICAL")) return;

        ShipScanner.ShipScanResult scan = ShipScanner.scanShip(player.getLocation().getBlock());

        if (scan.wireCount < 3) {
            player.sendMessage("§c[Tactical] §4Insufficient Power!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1f, 0.5f);
            return;
        }

        fireEnergyBeam(player);
    }

    private void fireEnergyBeam(Player player) {
        Location origin = player.getEyeLocation();
        Vector direction = origin.getDirection();

        player.playSound(origin, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1f, 2f);

        for (double i = 1; i < 40; i += 0.5) {
            Location point = origin.clone().add(direction.clone().multiply(i));

            // FIXED PARTICLE: Using REDSTONE (Older name for Dust)
            // If REDSTONE also fails, try Particle.valueOf("DUST") or Particle.valueOf("REDSTONE")
            try {
                player.getWorld().spawnParticle(Particle.REDSTONE, point, 1,
                        new Particle.DustOptions(Color.RED, 1.2f));
            } catch (Exception e) {
                // Absolute fallback for 1.20.5+
                player.getWorld().spawnParticle(Particle.valueOf("DUST"), point, 1,
                        new Particle.DustOptions(Color.RED, 1.2f));
            }

            if (point.getBlock().getType().isSolid()) {
                // FIXED PARTICLE: Using EXPLOSION_LARGE or HUGE
                player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, point, 1);
                player.getWorld().playSound(point, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
                break;
            }
        }
    }
}