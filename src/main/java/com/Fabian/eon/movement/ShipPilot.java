package com.Fabian.eon.movement;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import java.util.*;

public class ShipPilot {

    public static void moveShip(Set<Block> shipBlocks, Vector direction, Player pilot) {
        // 1. Directional Sorting (Crucial for collision and overwriting)
        List<Block> sortedBlocks = new ArrayList<>(shipBlocks);
        sortBlocksForMovement(sortedBlocks, direction);

        // 2. Capture "Snapshots" of the blocks
        // We use a LinkedHashMap to preserve the sorted order during the move
        LinkedHashMap<Location, BlockState> snapshots = new LinkedHashMap<>();
        for (Block b : sortedBlocks) {
            snapshots.put(b.getLocation().clone().add(direction), b.getState());
        }

        // 3. Move the Crew (Empathy/Candor check: we use a radius for now)
        for (Player p : pilot.getWorld().getPlayers()) {
            if (isOnShip(p, shipBlocks)) {
                p.teleport(p.getLocation().add(direction));
                // That small, non-annoying hum you requested
                p.playSound(p.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 0.2f, 0.6f);
            }
        }

        // 4. THE ATOMIC MOVE
        // First pass: Set new locations
        for (Map.Entry<Location, BlockState> entry : snapshots.entrySet()) {
            Location newLoc = entry.getKey();
            BlockState oldState = entry.getValue();

            Block newBlock = newLoc.getBlock();
            newBlock.setBlockData(oldState.getBlockData(), false);

            // Re-apply TileEntity Data (Chest contents, Beacon settings, etc.)
            BlockState newState = newBlock.getState();
            if (oldState instanceof Container && newState instanceof Container) {
                ((Container) newState).getSnapshotInventory().setContents(((Container) oldState).getInventory().getContents());
                newState.update(true);
            }
        }

        // Second pass: Clear old locations (only if a new block didn't move into it)
        Set<Location> newLocations = snapshots.keySet();
        for (Block b : shipBlocks) {
            if (!newLocations.contains(b.getLocation())) {
                b.setType(Material.AIR, false);
            }
        }
    }

    private static void sortBlocksForMovement(List<Block> blocks, Vector dir) {
        blocks.sort((a, b) -> {
            if (dir.getX() != 0) return Double.compare(b.getX() * dir.getX(), a.getX() * dir.getX());
            if (dir.getY() != 0) return Double.compare(b.getY() * dir.getY(), a.getY() * dir.getY());
            if (dir.getZ() != 0) return Double.compare(b.getZ() * dir.getZ(), a.getZ() * dir.getZ());
            return 0;
        });
    }

    private static boolean isOnShip(Player p, Set<Block> shipBlocks) {
        // Advanced: Check if the block under the player is part of the ship set
        return shipBlocks.contains(p.getLocation().getBlock().getRelative(0, -1, 0)) ||
                shipBlocks.contains(p.getLocation().getBlock());
    }
}