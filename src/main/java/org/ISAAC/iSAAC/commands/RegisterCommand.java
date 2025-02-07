package org.ISAAC.iSAAC.commands;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// import org.ISAAC.iSAAC.ISAAC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {

    // private ISAAC plugin;
    
    //     public RegisterCommand(ISAAC plugin) {
    //         this.plugin = plugin;
    // }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage : /register <password> <confirm_password>");
                return true;
            }

            String username = player.getName();
            String password = args[0];
            String confirm_password = args[1];

            Boolean registerSucces = registerWithAPI(username, password, confirm_password);

            if (registerSucces) {
                player.sendMessage(ChatColor.GREEN + "Enregistrement réussie !");
                player.sendMessage(ChatColor.GREEN + "Vous pouvez vous connecter maintenant.");
            } else {
                player.sendMessage(ChatColor.RED + "Erreur lors de l'enregistrement!");
            }
            return true;
        }
        return false;
    }

    private boolean registerWithAPI(String username, String password, String confirm_password) {
        try {
            String json = String.format("{\"mc_username\": \"%s\", \"password\": \"%s\", \"confirm_password\": \"%s\"}", username, password, confirm_password);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.isaac.erwansinck.com/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 201;
        } catch (IOException | InterruptedException | java.net.URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }
}   
