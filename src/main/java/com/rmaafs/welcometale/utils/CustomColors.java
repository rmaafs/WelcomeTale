/**
 * Utility for processing Minecraft color codes in text.
 * Supports standard color codes (&0-f) and basic formatting (bold, italic, reset).
 * 
 * @author github.com/rmaafs
 * @website https://rmaafs.com
 */
package com.rmaafs.welcometale.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hypixel.hytale.server.core.Message;

public class CustomColors {

    private static final Map<Character, Color> COLOR_MAP = new HashMap<>();
    private static final Pattern COLOR_PATTERN = Pattern.compile("[&§]([0-9a-fk-or])");

    static {
        COLOR_MAP.put('0', new Color(0x000000)); // Black
        COLOR_MAP.put('1', new Color(0x0000AA)); // Dark Blue
        COLOR_MAP.put('2', new Color(0x00AA00)); // Dark Green
        COLOR_MAP.put('3', new Color(0x00AAAA)); // Dark Aqua
        COLOR_MAP.put('4', new Color(0xAA0000)); // Dark Red
        COLOR_MAP.put('5', new Color(0xAA00AA)); // Dark Purple
        COLOR_MAP.put('6', new Color(0xFFAA00)); // Gold
        COLOR_MAP.put('7', new Color(0xAAAAAA)); // Gray
        COLOR_MAP.put('8', new Color(0x555555)); // Dark Gray
        COLOR_MAP.put('9', new Color(0x5555FF)); // Blue
        COLOR_MAP.put('a', new Color(0x55FF55)); // Green
        COLOR_MAP.put('b', new Color(0x55FFFF)); // Aqua
        COLOR_MAP.put('c', new Color(0xFF5555)); // Red
        COLOR_MAP.put('d', new Color(0xFF55FF)); // Light Purple
        COLOR_MAP.put('e', new Color(0xFFFF55)); // Yellow
        COLOR_MAP.put('f', new Color(0xFFFFFF)); // White
    }

    /**
     * Converts text with color codes into formatted Message object.
     * Supports & and § prefixes with color codes (0-9, a-f) and formatting (l=bold,
     * o=italic, r=reset).
     * 
     * @param text Input text with color codes
     * @return Formatted Message object with colors and styling applied
     */
    public static Message formatColorCodes(String text) {
        if (text == null || text.isEmpty()) {
            return Message.raw("");
        }

        List<Message> messages = new ArrayList<>();
        Matcher matcher = COLOR_PATTERN.matcher(text);

        int lastIndex = 0;
        Color currentColor = Color.WHITE;
        boolean bold = false;
        boolean italic = false;

        while (matcher.find()) {
            // Add text before the color code with current color and formatting
            if (matcher.start() > lastIndex) {
                String textSegment = text.substring(lastIndex, matcher.start());
                if (!textSegment.isEmpty()) {
                    Message msg = Message.raw(textSegment).color(currentColor);
                    if (bold)
                        msg = msg.bold(true);
                    if (italic)
                        msg = msg.italic(true);
                    messages.add(msg);
                }
            }

            // Update current color or formatting
            char colorCode = matcher.group(1).charAt(0);
            if (COLOR_MAP.containsKey(colorCode)) {
                currentColor = COLOR_MAP.get(colorCode);
            } else if (colorCode == 'r') {
                // Reset everything
                currentColor = Color.WHITE;
                bold = false;
                italic = false;
            } else if (colorCode == 'l') {
                bold = true;
            } else if (colorCode == 'o') {
                italic = true;
            }
            // Codes k, m, n not supported by Message API - ignored

            lastIndex = matcher.end();
        }

        // Add remaining text
        if (lastIndex < text.length()) {
            String textSegment = text.substring(lastIndex);
            if (!textSegment.isEmpty()) {
                Message msg = Message.raw(textSegment).color(currentColor);
                if (bold)
                    msg = msg.bold(true);
                if (italic)
                    msg = msg.italic(true);
                messages.add(msg);
            }
        }

        // If no messages were created (no text, only color codes), return empty message
        if (messages.isEmpty()) {
            return Message.raw("");
        }

        return Message.join(messages.toArray(new Message[0]));
    }

    /**
     * Removes all color codes from text.
     * 
     * @param text Input text with color codes
     * @return Plain text without formatting codes
     */
    public static String stripColorCodes(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return COLOR_PATTERN.matcher(text).replaceAll("");
    }
}
