package com.rmaafs.welcometale.listeners;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.rmaafs.welcometale.utils.CustomColors;
import com.rmaafs.welcometale.utils.FileConfiguration;

/**
 * Handles player-related events for the WelcomeTale plugin.
 * Manages welcome messages and join/quit message visibility.
 */
public class PlayerEvents {

    public PlayerEvents(JavaPlugin plugin) {
        this.registerEvents(plugin);
    }

    private void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
        plugin.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, this::onPlayerJoinWorld);
    }

    /**
     * Sends welcome message to player when they're ready.
     * Replaces {player} placeholder with player's display name.
     */
    private void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        String message = FileConfiguration.getConfig().getMessage().replace("{player}", player.getDisplayName());
        player.sendMessage(CustomColors.formatColorCodes(message));
    }

    /**
     * Controls visibility of join messages based on configuration.
     */
    private void onPlayerJoinWorld(AddPlayerToWorldEvent event) {
        event.setBroadcastJoinMessage(!FileConfiguration.getConfig().isDisableJoinMessage());
    }
}
