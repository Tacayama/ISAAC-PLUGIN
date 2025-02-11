package org.ISAAC.iSAAC.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishCommand implements CommandExecutor {

    private final Set<UUID> vanishedPlayers = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player targetPlayer = null;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Please specify a player name when running from the console.");

                return true;
            }

            targetPlayer = (Player) sender;

        } else {
            targetPlayer = Bukkit.getPlayer(args[0]);

            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Le joueur '" + args[0] + "' est introuvable.");

                return true;
            }
        }

        UUID uniqueId = targetPlayer.getUniqueId();
        boolean isVanished = vanishedPlayers.contains(uniqueId);

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (otherPlayer.equals(targetPlayer))
                continue;

            if (isVanished)
                otherPlayer.showPlayer(targetPlayer);
            else
                otherPlayer.hidePlayer(targetPlayer);
        }

        sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + "' est maintenant " + (isVanished ? "visible" : "vanished") + ".");

        if (isVanished)
            vanishedPlayers.remove(uniqueId);
        else
            vanishedPlayers.add(uniqueId);

        return true;
    }
}
