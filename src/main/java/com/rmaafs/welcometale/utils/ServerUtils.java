/**
 * @author github.com/rmaafs
 * @website https://rmaafs.com
 */

package com.rmaafs.welcometale.utils;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.PlayerUtil;

public class ServerUtils {
    public static void broadcast(@Nonnull Message message) {
        Universe.get().getWorlds().forEach((name, world) -> {
            PlayerUtil.broadcastMessageToPlayers(null, message, world.getEntityStore().getStore());
        });
    }

    public static boolean isOp(@Nonnull UUID playerUuid) {
        return PermissionsModule.get().getGroupsForUser(playerUuid).contains("OP");
    }

    public static boolean hasPermission(@Nonnull PlayerRef playerRef, @Nonnull String permission) {
        return PermissionsModule.get().hasPermission(playerRef.getUuid(), permission);
    }
}
