package com.rmaafs.welcometale.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.rmaafs.welcometale.Main;
import com.rmaafs.welcometale.utils.MessageFormatter;
import com.rmaafs.welcometale.utils.FileConfiguration;
import com.rmaafs.welcometale.utils.UpdateChecker;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Command to install the ClassTransformer early plugin.
 * Requires double confirmation within 10 minutes to apply the patch.
 * Usage: /welcometalepatch (first time shows warning, second time applies)
 */
public class PatchCommand extends CommandBase {

        public static final String PERMISSION = "welcometale.admin";
        private static final String TRANSFORMER_RESOURCE = "/earlyplugins/LeaveMessageTransformer.jar";
        private static final String TARGET_DIR = "earlyplugins";
        private static final String TARGET_FILE = "LeaveMessageTransformer.jar";
        private static final long CONFIRMATION_TIMEOUT_MINUTES = 10;
        private static final String DOCS_URL = new UpdateChecker("").REPO_URL + "/blob/main/leaveWorldMessagePatch.md";

        // Track pending confirmations: UUID -> timestamp of first execution
        private static final Map<UUID, Instant> pendingConfirmations = new HashMap<>();

        private static final String WARNING_MESSAGE = """
                        §e==============================================
                        §e§lWARNING
                        §fYou are about to install an §eearlyplugin§f that modifies
                        §fthe Hytale server's default behavior.

                        §eWhat it does:
                        §f• Disables the default "player left world" message
                        §f• Uses Hytale's official earlyplugins system
                        §f• Only affects the leave message broadcast

                        §fFor detailed technical documentation, visit:
                        §b§n""" + DOCS_URL + """
                        \n
                        §cUse at your own risk!
                        §fRun §e/welcometalepatch §fagain within §e10 minutes§f to confirm.
                        §fA server restart will be required after installation.
                        §e==============================================
                        """;

        private static final String SUCCESS_MESSAGE = """
                        §a==============================================
                        §a§lPATCH INSTALLED!
                        §fThe earlyplugin has been installed to:
                        §a{path}

                        §eServer restart required to apply changes.
                        §fAfter restart, the default leave messages will be disabled.
                        §a==============================================
                        """;

        public PatchCommand() {
                super("welcometalepatch", "Install the earlyplugin patch for custom leave messages", false);
        }

        @Override
        protected void executeSync(@Nonnull CommandContext commandContext) {
                if (!commandContext.sender().hasPermission(PERMISSION)) {
                        commandContext.sender()
                                        .sendMessage(MessageFormatter
                                                        .format(FileConfiguration.getConfig().getNoPermission()));
                        return;
                }

                UUID senderId = commandContext.sender().getUuid();
                Instant firstExecution = pendingConfirmations.get(senderId);

                // Second execution within timeout - apply patch
                if (firstExecution != null && Duration.between(firstExecution, Instant.now())
                                .toMinutes() < CONFIRMATION_TIMEOUT_MINUTES) {
                        applyPatch(commandContext);
                        pendingConfirmations.remove(senderId);
                        return;
                }

                // First execution or timeout expired - show warning
                pendingConfirmations.put(senderId, Instant.now());
                commandContext.sender().sendMessage(MessageFormatter.format(WARNING_MESSAGE));
        }

        /**
         * Applies the patch by extracting the transformer JAR to earlyplugins
         * directory.
         */
        private void applyPatch(@Nonnull CommandContext commandContext) {
                try {
                        // Create earlyplugins directory if it doesn't exist
                        Path earlyPluginsDir = Paths.get(TARGET_DIR);
                        if (!Files.exists(earlyPluginsDir)) {
                                Files.createDirectories(earlyPluginsDir);
                                Main.MAIN_INSTANCE.getLogger().atInfo()
                                                .log("Created earlyplugins directory at: "
                                                                + earlyPluginsDir.toAbsolutePath());
                        }

                        // Extract the embedded JAR
                        Path targetPath = earlyPluginsDir.resolve(TARGET_FILE);

                        // Check if patch is already installed
                        if (Files.exists(targetPath)) {
                                String alreadyInstalledMsg = """
                                                §e==============================================
                                                §e§lALREADY INSTALLED
                                                §fThe patch was previously installed at:
                                                §7""" + targetPath.toAbsolutePath() + """

                                                §fIf you need to reinstall, delete the file first.
                                                §e==============================================
                                                """;
                                commandContext.sender().sendMessage(MessageFormatter.format(alreadyInstalledMsg));
                                return;
                        }

                        try (InputStream resourceStream = getClass().getResourceAsStream(TRANSFORMER_RESOURCE)) {
                                if (resourceStream == null) {
                                        commandContext.sender().sendMessage(
                                                        MessageFormatter.format(
                                                                        "§cERROR: Transformer JAR not found in plugin resources"));
                                        Main.MAIN_INSTANCE.getLogger().atSevere()
                                                        .log("Transformer JAR not found in resources: "
                                                                        + TRANSFORMER_RESOURCE);
                                        return;
                                }

                                // Copy with replacement
                                Files.copy(resourceStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

                                Main.MAIN_INSTANCE.getLogger().atInfo()
                                                .log("Successfully installed transformer to: "
                                                                + targetPath.toAbsolutePath());

                                // Send success message
                                String successMsg = SUCCESS_MESSAGE.replace("{path}",
                                                targetPath.toAbsolutePath().toString());
                                commandContext.sender().sendMessage(MessageFormatter.format(successMsg));
                        }

                } catch (IOException e) {
                        commandContext.sender().sendMessage(
                                        MessageFormatter.format(
                                                        "§cERROR: Failed to install patch: " + e.getMessage()));
                        Main.MAIN_INSTANCE.getLogger().atSevere()
                                        .log("Failed to extract transformer JAR", e);
                } catch (SecurityException e) {
                        commandContext.sender().sendMessage(
                                        MessageFormatter.format(
                                                        "§cERROR: No permission to write to server directory"));
                        Main.MAIN_INSTANCE.getLogger().atSevere()
                                        .log("Permission denied when creating earlyplugins directory", e);
                }
        }
}
