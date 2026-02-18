package com.Fabian.eon.movement;

import com.Fabian.eon.systems.StationManager;
import com.Fabian.eon.utils.ShipScanner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class PilotSeat implements Listener {

    // 1. SPAWN THE CHAIR
    public static void spawnSeat(Player p) {
        Location loc = p.getLocation();

        // Remove any old boats nearby to prevent clutter
        loc.getWorld().getNearbyEntities(loc, 2, 2, 2).forEach(e -> {
            if (e instanceof Boat) e.remove();
        });

        // Spawn a Boat (Birch looks like a nice chair?)
        Boat seat = (Boat) loc.getWorld().spawnEntity(loc, EntityType.BOAT);
        seat.setGravity(false); // Make it float so it doesn't fall through the ship floor
        seat.addPassenger(p);   // Put the player in it immediately

        p.sendMessage("§e[Systems] §fWASD Controls Engaged. Press SHIFT to stand up.");
    }

    // 2. LISTEN FOR WASD (Vehicle Movement)
    @EventHandler
    public void onPilotSteer(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Boat)) return;

        Boat boat = (Boat) event.getVehicle();
        if (boat.getPassengers().isEmpty() || !(boat.getPassengers().get(0) instanceof Player)) return;

        Player pilot = (Player) boat.getPassengers().get(0);

        // Check if this player is actually the assigned Pilot
        if (!StationManager.isPilot(pilot)) return;

        // --- THE MOVEMENT LOGIC ---
        // We check if the boat has moved significantly
        Location from = event.getFrom();
        Location to = event.getTo();
        Vector movement = to.toVector().subtract(from.toVector());

        // Ignore tiny movements (jitter)
        if (movement.length() < 0.1) return;

        // Normalize to get Direction (North, South, East, West)
        // We round it so the ship moves in grid steps, not smooth curves (Minecraft limitation)
        Vector direction = new Vector(0, 0, 0);

        if (Math.abs(movement.getX()) > Math.abs(movement.getZ())) {
            direction.setX(movement.getX() > 0 ? 1 : -1);
        } else {
            direction.setZ(movement.getZ() > 0 ? 1 : -1);
        }

        // 3. MOVE THE SHIP
        // We scan from the boat's location
        ShipScanner.ShipScanResult result = ShipScanner.scanShip(boat.getLocation().getBlock());

        if (result.blocks.isEmpty()) return;

        // ACTUAL MOVE
        ShipPilot.moveShip(result.blocks, direction, pilot);

        // 4. RESET THE BOAT
        // This is the trick: We teleport the boat BACK to the center of the block
        // relative to the NEW ship position. This keeps the pilot "locked" to the grid.
        // (If we don't do this, the boat drifts off the ship).
        Location newSeatLoc = boat.getLocation().getBlock().getLocation().add(0.5, 0, 0.5).add(direction);
        boat.teleport(newSeatLoc);

        // Reset velocity so we don't slide uncontrollably
        boat.setVelocity(new Vector(0, 0, 0));


    }

    // 5. CLEANUP
    @EventHandler
    public void onDismount(VehicleExitEvent event) {
        if (event.getVehicle() instanceof Boat) {
            event.getVehicle().remove(); // Delete the boat when they stand up
            if (event.getExited() instanceof Player) {
                Player p = (Player) event.getExited();
                StationManager.setStation(p, "NONE"); // Resign from Helm
                p.sendMessage("§e[Systems] §fStanding down from Helm.");
            }

        }

    }

}
