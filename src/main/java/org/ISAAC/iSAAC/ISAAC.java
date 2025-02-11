    package org.ISAAC.iSAAC;

    import org.ISAAC.iSAAC.commands.LoginCommand;
    import org.ISAAC.iSAAC.commands.RegisterCommand;
    import org.bukkit.Bukkit;
    import org.bukkit.ChatColor;
    import org.bukkit.entity.Player;
    import org.bukkit.event.EventHandler;
    import org.bukkit.event.Listener;
    import org.bukkit.event.block.BlockBreakEvent;
    import org.bukkit.event.player.AsyncPlayerChatEvent;
    import org.bukkit.event.player.PlayerJoinEvent;
    import org.bukkit.event.player.PlayerMoveEvent;
    import org.bukkit.event.player.PlayerQuitEvent;
    import org.bukkit.plugin.java.JavaPlugin;
    import org.bukkit.scoreboard.Scoreboard;
    import org.bukkit.scoreboard.ScoreboardManager;
    import org.bukkit.scoreboard.Team;

    import java.util.HashMap;
    import java.util.HashSet;
    import java.util.UUID;

    public final class ISAAC extends JavaPlugin implements Listener {

        private RankManager rankManager;
        private Scoreboard scoreboard;

        public final HashSet<UUID> loggedInPlayers = new HashSet<>();
        public final HashSet<UUID> WarnedPlayer = new HashSet<>(); 
        public final HashMap<UUID, RANK> ranklist = new HashMap<>();

        @Override
        public void onEnable() {
            rankManager = new RankManager(this);

            getServer().getPluginManager().registerEvents(this, this);
            getLogger().info("ISAAC Ready !");
            
            this.getCommand("login").setExecutor(new LoginCommand(this, rankManager));
            this.getCommand("register").setExecutor(new RegisterCommand());
            
        }

        @Override
        public void onDisable() {
            getLogger().info("ISAAC désactivé !");
        }

        @SuppressWarnings("deprecation")
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            setPlayerTabRank(event.getPlayer(), null);
            event.getPlayer().sendMessage(ChatColor.BLUE + "Bienvenue sur le serveur MC de Dhalsiiim!");
            event.getPlayer().sendMessage(ChatColor.BLUE + "Connectez-vous avec la commande : /login <password>");
            event.getPlayer().sendMessage(ChatColor.BLUE + "Si vous n'avez pas de compte, enregistrez-vous avec : /register <password> <confirm password>");
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            WarnedPlayer.remove(event.getPlayer().getUniqueId());
            loggedInPlayers.remove(event.getPlayer().getUniqueId());
            ranklist.remove(event.getPlayer().getUniqueId());
        }

        private boolean isPlayerLoggedIn(Player player) {
            UUID playerUuid = player.getUniqueId();
            return loggedInPlayers.contains(playerUuid);
        }

        @SuppressWarnings("deprecation")
        private void isPlayerWarned(Player player) {
            if (!WarnedPlayer.contains(player.getUniqueId())) {
                WarnedPlayer.add(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Vous devez être connecté ! Utilisez /login <password>");
            }
        }

        @EventHandler
        public void onPlayerMoove(PlayerMoveEvent event) {
            if (!isPlayerLoggedIn(event.getPlayer())) {
                isPlayerWarned(event.getPlayer());
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onPlayerBreakBlock(BlockBreakEvent event) {
            if (!isPlayerLoggedIn(event.getPlayer())) {
                isPlayerWarned(event.getPlayer());
                event.setCancelled(true);
            }
        }

        @SuppressWarnings("deprecation")
        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            Player player = event.getPlayer();
            RANK rank = ranklist.get(player.getUniqueId());

            String formattedMessage = rank.getColor() + "[" + rank.getName() + "] " + ChatColor.RESET + player.getName() + ChatColor.WHITE + " : " + event.getMessage();
            
            event.setFormat(formattedMessage);
        }

        public void setupScoreboard() {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            scoreboard = manager.getNewScoreboard();
        }

        @SuppressWarnings("deprecation")
        public void setPlayerTabRank(Player player, RANK rank) {
            if (scoreboard == null) {
                setupScoreboard();
            }

            Team team = scoreboard.getTeam(player.getName());
            if (team == null) {
                team = scoreboard.registerNewTeam(player.getName());
            }
            team.addEntry(player.getName());

            if (rank == null) {
                team.setPrefix(ChatColor.DARK_RED + " [Non Connecté] ");
            } else {
                switch (rank.getName().toUpperCase()) {
                    case "ADMIN":
                        team.setPrefix(ChatColor.RED + " [ADMIN] ");
                        break;
                    case "MODO":
                        team.setPrefix(ChatColor.BLUE + " [MODO] ");
                        break;
                    case "TWITCH":
                        team.setPrefix(ChatColor.LIGHT_PURPLE + " [TWITCH] ");
                        break;
                    default:
                        team.setPrefix(ChatColor.GRAY + " [NORMAL] ");
                        break;
                }
            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.setScoreboard(scoreboard);
            }
        }
    }
