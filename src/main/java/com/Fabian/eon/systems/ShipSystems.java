package com.Fabian.eon.systems;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class ShipSystems {

    // --- 1. The Power Grid (End Rods) ---
    // Power is "On/Off". If you have enough rods connecting the core to the engine, it works.
    public static boolean isConductive(Material mat) {
        return mat == Material.END_ROD || mat == Material.SPONGE; // Sponge is the "Junction Box"
    }

    // --- 2. The Fluid System (Copper Pipes) ---
    // Fluids are "Efficiency Based". Rust = Friction/Leaks.
    public static double getFluidEfficiency(Block block) {
        String type = block.getType().name();

        if (!type.contains("COPPER")) return 0.0;

        // Waxed = Sealed and protected
        if (type.contains("WAXED")) return 1.0;

        // Unwaxed = Susceptible to space corrosion
        if (type.startsWith("OXIDIZED_")) return 0.25;  // Clogged/Leaking
        if (type.startsWith("WEATHERED_")) return 0.50; // Flow reduced
        if (type.startsWith("EXPOSED_")) return 0.75;   // Minor leaks

        return 1.0; // Brand new copper
    }
}