package dev.poetty.staffutils;

import dev.poetty.staffutils.command.StaffTime;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public class StaffUtils extends Plugin implements Listener {


    private StaffTime tiempoCommand;
    private Configuration config;

    @Override
    public void onEnable() {
        loadConfig();


        StaffTime tiempoCommand = new StaffTime(this);
        this.getProxy().getPluginManager().registerCommand(this, tiempoCommand);
        this.getProxy().getPluginManager().registerListener(this, new PlayerListener(tiempoCommand));

        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7[&cINFO&7] &aIniciando Datos..."));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', ""));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------------------"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', ""));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&b&lStaffUtils &8&l- &fBungee Plugin"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', ""));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&aEl plugin se ha iniciado correctamente."));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', ""));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&8&l- &fCreator&7: &fPoetty"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&8&l- &fVersion&7: &f1.0.1"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------------------"));

        // Cargamos los datos de tiempo de juego al iniciar el plugin
        tiempoCommand.loadPlayerData();
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7[&cINFO&7] &aGuardando Datos..."));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', ""));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------------------"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', ""));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&b&lStaffUtils &8&l- &fBungee Plugin"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', ""));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&aEl plugin se ha apagado correctamente."));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', ""));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&8&l- &fCreator&7: &fPoetty"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&8&l- &fVersion&7: &f1.0.1"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------------------"));

    }

    // Método para cargar la configuración desde config.yml
    private void loadConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                try (InputStream inputStream = getResourceAsStream("config.yml");
                     OutputStream outputStream = new FileOutputStream(configFile)) {
                    byte[] buffer = new byte[inputStream.available()];
                    inputStream.read(buffer);
                    outputStream.write(buffer);
                }
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Método para recargar la configuración desde config.yml
    public void reloadConfig() {
        try {
            File configFile = new File(getDataFolder(), "config.yml");
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfig() {
        return config;
    }

}