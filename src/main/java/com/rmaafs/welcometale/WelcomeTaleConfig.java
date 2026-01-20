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
                        .append(new KeyedCodec<String>("JoinMessage", MESSAGE_CODEC),
                                        (config, value, info) -> config.joinMessage = value,
                                        (config, info) -> config.joinMessage)
                        .add()
                        .append(new KeyedCodec<String>("WelcomePlayerMessage", MESSAGE_CODEC),
                                        (config, value, info) -> config.welcomePlayerMessage = value,
                                        (config, info) -> config.welcomePlayerMessage)
                        .add()
                        .append(new KeyedCodec<String>("LeaveMessage", MESSAGE_CODEC),
                                        (config, value, info) -> config.leaveMessage = value,
                                        (config, info) -> config.leaveMessage)
                        .add()
                        .append(new KeyedCodec<Boolean>("DisableDefaultJoinMessage", Codec.BOOLEAN),
                                        (config, value, info) -> config.disableDefaultJoinMessage = value,
                                        (config, info) -> config.disableDefaultJoinMessage)
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

        private String joinMessage = "&3&l > &3{player} &bjoined";
        private String welcomePlayerMessage = "&7Welcome &a{player} &7to the server!";
        private String leaveMessage = "&4&l > &4{player} &cleft";
        private boolean disableDefaultJoinMessage = true;
        private String messageReloaded = "&aConfiguration reloaded successfully!";
        private String noPermission = "&cYou don't have permission to use this command!";

        /**
         * @return Join broadcast message with {player} placeholder support
         */
        public String getJoinMessage() {
                return joinMessage;
        }

        /**
         * @return Welcome private message sent to player with {player} placeholder
         *         support
         */
        public String getWelcomePlayerMessage() {
                return welcomePlayerMessage;
        }

        /**
         * @return Leave broadcast message with {player} placeholder support
         */
        public String getLeaveMessage() {
                return leaveMessage;
        }

        /**
         * @return true if default join messages should be disabled
         */
        public boolean isDisableDefaultJoinMessage() {
                return disableDefaultJoinMessage;
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
