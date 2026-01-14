package com.rmaafs.welcometale;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.function.FunctionCodec;

/**
 * Configuration class for WelcomeTale plugin.
 * Handles all configurable settings including messages, permissions, and
 * join/quit options.
 */
public class WelcomeTaleConfig {

        private static final FunctionCodec<String[], String> MESSAGE_CODEC = new FunctionCodec<>(
                        Codec.STRING_ARRAY,
                        arr -> String.join("\n", arr),
                        str -> str.split("\n"));

        public static final BuilderCodec<WelcomeTaleConfig> CODEC = BuilderCodec
                        .builder(WelcomeTaleConfig.class, WelcomeTaleConfig::new)
                        .append(new KeyedCodec<String>("Message", MESSAGE_CODEC),
                                        (config, value, info) -> config.message = value,
                                        (config, info) -> config.message)
                        .add()
                        .append(new KeyedCodec<Boolean>("DisableJoinMessage", Codec.BOOLEAN),
                                        (config, value, info) -> config.disableJoinMessage = value,
                                        (config, info) -> config.disableJoinMessage)
                        .add()
                        .append(new KeyedCodec<String>("MessageReloaded", Codec.STRING),
                                        (config, value, info) -> config.messageReloaded = value,
                                        (config, info) -> config.messageReloaded)
                        .add()
                        .append(new KeyedCodec<String>("NoPermission", Codec.STRING),
                                        (config, value, info) -> config.noPermission = value,
                                        (config, info) -> config.noPermission)
                        .add()
                        .build();

        private String message = "&7Welcome &a{player} to the server!";
        private boolean disableJoinMessage = true;
        private String messageReloaded = "&aConfiguration reloaded successfully!";
        private String noPermission = "&cYou don't have permission to use this command!";

        public WelcomeTaleConfig() {
        }

        /**
         * @return Welcome message with {player} placeholder support
         */
        public String getMessage() {
                return message;
        }

        /**
         * @return true if default join messages should be disabled
         */
        public boolean isDisableJoinMessage() {
                return disableJoinMessage;
        }

        /**
         * @return Message displayed when configuration is reloaded
         */
        public String getMessageReloaded() {
                return messageReloaded;
        }

        /**
         * @return Message displayed when player lacks permission
         */
        public String getNoPermission() {
                return noPermission;
        }
}
