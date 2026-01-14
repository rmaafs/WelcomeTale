/**
 * Manages plugin configuration loading, reloading, and file operations.
 * Handles copying example config files to the mods directory.
 * 
 * @author github.com/rmaafs
 * @website https://rmaafs.com
 */
package com.rmaafs.welcometale.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.util.Config;
import com.rmaafs.welcometale.WelcomeTaleConfig;

public class FileConfiguration {

    private static Config<WelcomeTaleConfig> config;
    private static JavaPlugin pluginInstance;

    /**
     * Initializes configuration system with plugin instance and config object.
     * Copies example config to mods folder on first run.
     */
    public static void initialize(JavaPlugin plugin, Config<WelcomeTaleConfig> config) {
        pluginInstance = plugin;

        copyConfigToModsFolder(plugin);
        FileConfiguration.config = config;
    }

    /**
     * Reloads configuration from disk and updates example config file.
     */
    public static void reloadConfig() {
        copyConfigToModsFolder(pluginInstance);
        config.load();

        pluginInstance.getLogger().atInfo().log(CustomColors.stripColorCodes(config.get().getMessageReloaded()));
    }

    /**
     * @return Current loaded configuration instance
     */
    public static WelcomeTaleConfig getConfig() {
        return config.get();
    }

    /**
     * Copies example config file from resources to mods directory.
     * Creates necessary directories if they don't exist.
     */
    public static void copyConfigToModsFolder(JavaPlugin plugin) {
        final String configFileName = "config.example.json";
        try {
            String group = plugin.getManifest().getGroup();
            String name = plugin.getManifest().getName();
            Path modsPluginDir = Path.of("mods", group + "_" + name);
            Path targetConfig = modsPluginDir.resolve(configFileName);

            if (!Files.exists(modsPluginDir)) {
                Files.createDirectories(modsPluginDir);
            }

            if (Files.exists(targetConfig)) {
                Files.delete(targetConfig);
            }

            var resource = plugin.getClass().getResourceAsStream("/" + configFileName);
            if (resource != null) {
                Files.copy(resource, targetConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
