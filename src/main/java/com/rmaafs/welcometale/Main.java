package com.rmaafs.welcometale;

import javax.annotation.Nonnull;

import com.rmaafs.welcometale.commands.PatchCommand;
import com.rmaafs.welcometale.commands.WelcomeTaleCommand;
import com.rmaafs.welcometale.listeners.PlayerEvents;
import com.rmaafs.welcometale.utils.FileConfiguration;
import com.rmaafs.welcometale.utils.UpdateChecker;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

/**
 * Main plugin class for WelcomeTale.
 * Manages plugin initialization, configuration, and registration of commands
 * and events.
 */
public class Main extends JavaPlugin {

    public static Main MAIN_INSTANCE = null;
    public static UpdateChecker updateChecker = null;

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
        MAIN_INSTANCE = this;

        FileConfiguration.initialize(this, this.withConfig("config", WelcomeTaleConfig.CODEC));

        getLogger().atInfo().log(
                ">>>  " + this.getManifest().getName() + " v" + this.getManifest().getVersion().toString()
                        + " by rmaafs  <<<");
    }

    /**
     * Setup method called during plugin initialization.
     * Registers commands and event listeners.
     */
    @Override
    protected void setup() {
        this.registerCommands();
        this.registerEvents();

        if (FileConfiguration.getConfig().isCheckForUpdates()) {
            updateChecker = new UpdateChecker(this.getManifest().getVersion().toString());
            if (!updateChecker.isUsingLatest()) {
                getLogger().atWarning().log("════════════════════ WelcomeTale ═════════════════════");
                getLogger().atWarning()
                        .log("New version available! (v" + updateChecker.getLatestVersion() + ") - Current: (v"
                                + updateChecker.getCurrentVersion() + ")");
                getLogger().atWarning().log("Download it at " + updateChecker.REPO_URL + "/releases");
                getLogger().atWarning().log("════════════════════ WelcomeTale ═════════════════════");
            }
        }
    }

    private void registerCommands() {
        this.getCommandRegistry().registerCommand(new WelcomeTaleCommand());
        this.getCommandRegistry().registerCommand(new PatchCommand());
    }

    private void registerEvents() {
        new PlayerEvents(this);
    }
}