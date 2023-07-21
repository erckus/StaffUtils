package dev.poetty.staffutils.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaffTime extends Command {

    private Plugin plugin;
    private Map<UUID, Long> playerLoginTimes;
    private Map<UUID, Long> playerSessionStartTimes;
    private final String dataFolderPath;
    private Configuration config;


    public StaffTime(Plugin plugin) {
        super("tiempo", "staffutils.admin");
        this.plugin = plugin;
        playerLoginTimes = new HashMap<>();
        playerSessionStartTimes = new HashMap<>();
        dataFolderPath = plugin.getDataFolder().getPath() + File.separator + "userdata" + File.separator;

        // Cargamos los datos de tiempo de juego al iniciar el plugin.
        loadPlayerData();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            String noPlayerMessage = config.getString("messages.no_player");
            if (noPlayerMessage != null && !noPlayerMessage.isEmpty()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPlayerMessage));
            }
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!sender.hasPermission("staffutils.admin")) {
            String noPermissionMessage = config.getString("messages.no_permission");
            if (noPermissionMessage != null && !noPermissionMessage.isEmpty()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionMessage));
            }
            return;
        }

        if (!sender.hasPermission("staffutils.admin")) {
            String noPermissionMessage = config.getString("messages.no_permission");
            if (noPermissionMessage != null && !noPermissionMessage.isEmpty()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionMessage));
            }
            return;
        }

        if (args.length == 0) {
            showPlayerTime(player, sender);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("resetall")) {
                if (sender.hasPermission("staffutils.resetall")) {
                    resetAllPlayers();
                    String resetAllSuccessMessage = config.getString("messages.reset_all_success");
                    if (resetAllSuccessMessage != null && !resetAllSuccessMessage.isEmpty()) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', resetAllSuccessMessage));
                    }
                } else {
                    String noResetAllPermissionMessage = config.getString("messages.no_reset_all_permission");
                    if (noResetAllPermissionMessage != null && !noResetAllPermissionMessage.isEmpty()) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noResetAllPermissionMessage));
                    }
                }
            } else {
                ProxiedPlayer targetPlayer = plugin.getProxy().getPlayer(args[0]);
                if (targetPlayer != null) {
                    showPlayerTime(targetPlayer, sender);
                } else {
                    String playerNotFoundMessage = config.getString("messages.player_not_found");
                    if (playerNotFoundMessage != null && !playerNotFoundMessage.isEmpty()) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', playerNotFoundMessage));
                    }
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            if (sender.hasPermission("staffutils.reset")) {
                String playerName = args[1];
                resetPlayer(playerName);
                String resetPlayerSuccessMessage = config.getString("messages.reset_player_success");
                if (resetPlayerSuccessMessage != null && !resetPlayerSuccessMessage.isEmpty()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', resetPlayerSuccessMessage.replace("%player%", playerName)));
                }
            } else {
                String noResetPermissionMessage = config.getString("messages.no_reset_permission");
                if (noResetPermissionMessage != null && !noResetPermissionMessage.isEmpty()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noResetPermissionMessage));
                }
            }
        } else {
            String usageMessage = config.getString("messages.usage");
            if (usageMessage != null && !usageMessage.isEmpty()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', usageMessage));
            }
        }
    }

    private void showPlayerTime(ProxiedPlayer player, CommandSender sender) {
        UUID playerUUID = player.getUniqueId();
        if (!playerLoginTimes.containsKey(playerUUID)) {
            playerLoginTimes.put(playerUUID, 0L);
        }

        long totalTimeMillis = playerLoginTimes.get(playerUUID);
        long sessionStartTime = playerSessionStartTimes.getOrDefault(playerUUID, System.currentTimeMillis());
        long sessionTimeMillis = System.currentTimeMillis() - sessionStartTime;
        long sessionTimeHours = sessionTimeMillis / 3600000; // 1000ms * 60s * 60m = 3600000ms

        long totalTimeHours = (totalTimeMillis + sessionTimeMillis) / 3600000;

        // Obtener el mensaje desde el archivo de configuración
        String playerTimeMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.player_time"));

        // Reemplazar las variables del mensaje con los valores adecuados
        playerTimeMessage = playerTimeMessage.replace("%player%", player.getName());
        playerTimeMessage = playerTimeMessage.replace("%time%", String.valueOf(totalTimeHours));

        sender.sendMessage(playerTimeMessage);
    }


    public void registerLogoutTime(ProxiedPlayer player) {
        UUID playerUUID = player.getUniqueId();
        if (playerSessionStartTimes.containsKey(playerUUID)) {
            long sessionStartTime = playerSessionStartTimes.get(playerUUID);
            long sessionTimeMillis = System.currentTimeMillis() - sessionStartTime;
            playerSessionStartTimes.remove(playerUUID);

            long totalTimeMillis = playerLoginTimes.getOrDefault(playerUUID, 0L);
            totalTimeMillis += sessionTimeMillis;
            playerLoginTimes.put(playerUUID, totalTimeMillis);

            savePlayerData(player);
        }
    }

    private void savePlayerData(ProxiedPlayer player) {
        UUID playerUUID = player.getUniqueId();
        long totalTimeMillis = playerLoginTimes.getOrDefault(playerUUID, 0L);

        File dataFolder = new File(dataFolderPath);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File playerFile = new File(dataFolderPath + playerUUID + ".txt");
        try {
            Files.write(playerFile.toPath(), String.valueOf(totalTimeMillis).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPlayerData() {
        File dataFolder = new File(dataFolderPath);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File[] playerFiles = dataFolder.listFiles();
        if (playerFiles != null) {
            for (File playerFile : playerFiles) {
                if (playerFile.isFile() && playerFile.getName().endsWith(".txt")) {
                    String playerName = playerFile.getName().replace(".txt", "");
                    try (BufferedReader reader = Files.newBufferedReader(playerFile.toPath())) {
                        StringBuilder contentBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            contentBuilder.append(line);
                        }
                        String content = contentBuilder.toString();
                        if (content != null && !content.isEmpty()) {
                            long totalTimeMillis = Long.parseLong(content);
                            playerLoginTimes.put(UUID.fromString(playerName), totalTimeMillis);
                        }
                    } catch (IOException | NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void resetAllPlayers() {
        playerLoginTimes.clear();
        playerSessionStartTimes.clear();

        File dataFolder = new File(dataFolderPath);
        if (dataFolder.exists() && dataFolder.isDirectory()) {
            File[] playerFiles = dataFolder.listFiles();
            if (playerFiles != null) {
                for (File playerFile : playerFiles) {
                    if (playerFile.isFile() && playerFile.getName().endsWith(".txt")) {
                        playerFile.delete();
                    }
                }
            }
        }
    }

    private void resetPlayer(String playerName) {
        UUID playerUUID = getUUIDFromName(playerName);
        if (playerUUID != null) {
            playerLoginTimes.remove(playerUUID);
            playerSessionStartTimes.remove(playerUUID);

            File playerFile = new File(dataFolderPath + playerUUID + ".txt");
            if (playerFile.exists()) {
                playerFile.delete();
            }
        }
    }

    private UUID getUUIDFromName(String playerName) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(playerName);
        if (player != null) {
            return player.getUniqueId();
        }
        return null;
    }
}
