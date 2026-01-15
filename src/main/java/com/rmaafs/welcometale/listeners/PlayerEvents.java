package com.rmaafs.welcometale.listeners;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.world.PlayerUtil;
import com.rmaafs.welcometale.utils.MessageFormatter;
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
        String playerName = player.getDisplayName();

        String joinMessage = FileConfiguration.getConfig().getJoinMessage().replace("{player}", playerName);
        String welcomeMessage = FileConfiguration.getConfig().getWelcomePlayerMessage().replace("{player}", playerName);

        if (!joinMessage.trim().isEmpty()) {
            PlayerUtil.broadcastMessageToPlayers(null, MessageFormatter.format(joinMessage),
                    player.getWorld().getEntityStore().getStore());
        }

        if (!welcomeMessage.trim().isEmpty()) {
            player.sendMessage(MessageFormatter.format(welcomeMessage));
        }
    }

    /**
     * Controls visibility of join messages based on configuration.
     */
    private void onPlayerJoinWorld(AddPlayerToWorldEvent event) {
        event.setBroadcastJoinMessage(!FileConfiguration.getConfig().isDisableDefaultJoinMessage());
    }
}
