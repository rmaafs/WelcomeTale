package com.rmaafs.welcometale.commands;

import javax.annotation.Nonnull;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.rmaafs.welcometale.utils.MessageFormatter;
import com.rmaafs.welcometale.utils.FileConfiguration;

/**
 * Command handler for /welcometale.
 * Allows reloading plugin configuration with proper permission check.
 */
public class WelcomeTaleCommand extends CommandBase {

    public static final String PERMISSION = "welcometale.reload";

    public WelcomeTaleCommand() {
        super("welcometale", "WelcomeTale command", false);
    }

    /**
     * Executes the command to reload configuration.
     * Requires permission.
     */
    @Override
    protected void executeSync(@Nonnull CommandContext commandContext) {
        if (!commandContext.sender().hasPermission(PERMISSION)) {
            commandContext.sender()
                    .sendMessage(MessageFormatter.format(FileConfiguration.getConfig().getNoPermission()));
            return;
        }

        FileConfiguration.reloadConfig();
        commandContext.sender()
                .sendMessage(MessageFormatter.format(FileConfiguration.getConfig().getMessageReloaded()));
    }
}