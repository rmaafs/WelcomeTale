package com.rmaafs.welcometale.listeners;

import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
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
        plugin.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, this::onPlayerJoinWorld);
        plugin.getEventRegistry().registerGlobal(PlayerConnectEvent.class, this::onPlayerConnect);
        plugin.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
    }

    /**
     * Controls visibility of join messages based on configuration.
     */
    private void onPlayerJoinWorld(AddPlayerToWorldEvent event) {
        event.setBroadcastJoinMessage(!FileConfiguration.getConfig().isDisableDefaultJoinMessage());
    }

    /**
     * Handles player connection events by sending custom welcome and join messages.
     * Broadcasts a join message to all players and sends a private welcome message
     * to the connecting player. Messages are formatted with the player's username
     * replacing the {player} placeholder.
     *
     * @param event the player connect event containing player and world information
     */
    private void onPlayerConnect(PlayerConnectEvent event) {
        PlayerRef player = event.getPlayerRef();
        String playerName = player.getUsername();

        String joinMessage = FileConfiguration.getConfig().getJoinMessage().replace("{player}", playerName);
        String welcomeMessage = FileConfiguration.getConfig().getWelcomePlayerMessage().replace("{player}", playerName);

        if (!joinMessage.trim().isEmpty()) {
            PlayerUtil.broadcastMessageToPlayers(null, MessageFormatter.format(joinMessage),
                    event.getWorld().getEntityStore().getStore());
        }

        if (!welcomeMessage.trim().isEmpty()) {
            player.sendMessage(MessageFormatter.format(welcomeMessage));
        }
    }

    /**
     * Handles player disconnection events by broadcasting a custom leave message.
     * The message is formatted with the player's username replacing the {player}
     * placeholder.
     *
     * @param event the player disconnect event containing player information
     */
    private void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerRef player = event.getPlayerRef();
        String playerName = player.getUsername();

        String leaveMessage = FileConfiguration.getConfig().getLeaveMessage().replace("{player}", playerName);

        if (!leaveMessage.trim().isEmpty()) {
            PlayerUtil.broadcastMessageToPlayers(null, MessageFormatter.format(leaveMessage),
                    Universe.get().getWorld(player.getWorldUuid()).getEntityStore().getStore());
        }
    }
}
