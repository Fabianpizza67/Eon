package com.Fabian.eon.utils;

import com.Fabian.eon.systems.ShipSystems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ShipScanner {

    private static final int MAX_BLOCKS = 5000;

    public static class ShipScanResult {
        public Set<Block> blocks = new HashSet<>();
        public int pipeCount = 0;
        public double totalPipeEfficiency = 0.0;
        public int wireCount = 0;
        public int terminalCount = 0;

        public double getPlumbingHealth() {
            if (pipeCount == 0) return 1.0;
            return totalPipeEfficiency / pipeCount;
        }
    }

    public static ShipScanResult scanShip(Block startBlock) {
        ShipScanResult result = new ShipScanResult();
        Queue<Block> toCheck = new LinkedList<>();

        if (startBlock == null || startBlock.getType() == Material.AIR) return result;

        toCheck.add(startBlock);
        result.blocks.add(startBlock);

        BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

        while (!toCheck.isEmpty()) {
            Block current = toCheck.poll();

            if (result.blocks.size() >= MAX_BLOCKS) break;

            Material type = current.getType();

            // 1. Detect Terminals
            if (type == Material.BEACON) {
                result.terminalCount++;
            }

            // 2. Detect Power Grid (Neural Network)
            // Changed GridManager.isWire to ShipSystems.isConductive
            if (ShipSystems.isConductive(type)) {
                result.wireCount++;
            }

            // 3. Detect Fluid Systems (Vascular System)
            // Changed GridManager.getPipeEfficiency to ShipSystems.getFluidEfficiency
            double efficiency = ShipSystems.getFluidEfficiency(current);
            if (efficiency > 0) {
                result.pipeCount++;
                result.totalPipeEfficiency += efficiency;
            }

            for (BlockFace face : faces) {
                Block neighbor = current.getRelative(face);
                if (!neighbor.getType().isAir() && !result.blocks.contains(neighbor)) {
                    result.blocks.add(neighbor);
                    toCheck.add(neighbor);
                }
            }
        }
        return result;
    }
}