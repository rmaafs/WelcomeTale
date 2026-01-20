package com.rmaafs.welcometale.utils;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.PlayerUtil;

public class ServerUtils {
    public static void broadcast(Message message) {
        Universe.get().getWorlds().forEach((name, world) -> {
            PlayerUtil.broadcastMessageToPlayers(null, message, world.getEntityStore().getStore());
        });
    }
}
