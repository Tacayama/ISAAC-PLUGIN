package org.ISAAC.iSAAC.commands;


import org.ISAAC.iSAAC.ISAAC;
import org.ISAAC.iSAAC.RankManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginCommand implements CommandExecutor {

    private ISAAC plugin;
    private final RankManager rankManager;

    
    public LoginCommand(ISAAC plugin, RankManager rankManager) {
            this.plugin = plugin;
            this.rankManager = rankManager;
    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /login <password>");
                return true;
            }

            String password = args[0];

            boolean loginSuccess = authWithApi(player, password);

            if (loginSuccess) {
                plugin.loggedInPlayers.add(player.getUniqueId());
                plugin.setPlayerTabRank(player, plugin.ranklist.get(player.getUniqueId()));
                player.updateCommands();
                player.sendMessage(ChatColor.GREEN + "Connexion réussie !");
            } else {
                player.sendMessage(ChatColor.RED + "Identifiants incorrects !");
            }
            return false;
        } else {
            sender.sendMessage(ChatColor.RED + "Cette commande est réservée aux joueurs.");
            return false;
        }
    }

    public boolean authWithApi(Player player, String password) {
        try {
            String json = String.format("{\"mc_username\": \"%s\", \"password\": \"%s\"}", player.getName(), password);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.isaac.erwansinck.com/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                String responseBody = response.body();
                String rank = extractRankFromJson(responseBody);
                Boolean succes = extractSuccesFromJson(responseBody);
                if (succes) {
                    rankManager.AddToRANKlist(player, rank);
                    return true;
                }
                return false;
            }
            return false;
        } catch (IOException | InterruptedException | java.net.URISyntaxException e) {
            return false;

        }
    }

    private String extractRankFromJson(String json) {
        Pattern pattern = Pattern.compile("\"rank\"\\s*:\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private boolean extractSuccesFromJson(String json) {
        Pattern pattern = Pattern.compile("\"result\"\\s*:\\s*(true|false)");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return false;
    }
}
