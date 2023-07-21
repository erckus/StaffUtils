package dev.poetty.staffutils;

import dev.poetty.staffutils.command.StaffTime;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {

    private StaffTime tiempoCommand;

    public PlayerListener(StaffTime tiempoCommand) {
        this.tiempoCommand = tiempoCommand;
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        tiempoCommand.registerLogoutTime(player);
    }
}