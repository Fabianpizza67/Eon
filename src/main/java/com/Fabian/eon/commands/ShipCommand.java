package com.Fabian.eon.commands;

import com.Fabian.eon.utils.ShipScanner;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ShipCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Command: /ship scan
// Inside onCommand...
        if (args[0].equalsIgnoreCase("scan")) {
            Block target = player.getTargetBlockExact(5);
            // ... null checks ...

            player.sendMessage("§e[Eon] §7Running Diagnostics...");

            // Use the new Result Class
            ShipScanner.ShipScanResult scan = ShipScanner.scanShip(target);

            player.sendMessage("§aScan Complete!");
            player.sendMessage("§7Total Mass: §f" + scan.blocks.size() + " blocks");

            if (scan.terminalCount > 0) {
                player.sendMessage("§bComputer Terminals: §a" + scan.terminalCount + " ONLINE");
            } else {
                player.sendMessage("§cMain Computer: §4OFFLINE (Ship needs at least one Beacon!)");
            }

            player.sendMessage("§7Wiring Nodes: §e" + scan.wireCount);

            // The Pipe Logic
            if (scan.pipeCount > 0) {
                double health = scan.getPlumbingHealth() * 100;
                String color = "§a";
                if (health < 80) color = "§e"; // Yellow warning
                if (health < 50) color = "§c"; // Red warning

                player.sendMessage("§7Fluid Systems Efficiency: " + color + String.format("%.1f", health) + "%");

                if (health < 50) {
                    player.sendMessage("§4[WARNING] §cPipe Oxidation Detected! Maintenance Required!");
                }
            } else {
                player.sendMessage("§7Fluid Systems: §8None Detected");
            }

            return true;
        }
    return true; }
}