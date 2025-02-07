package org.ISAAC.iSAAC;

import java.util.HashSet;

import net.md_5.bungee.api.ChatColor;

public class RANK {
    private final String name;
    private final ChatColor color;
    private final String prefix;
    private final int priority;
    final HashSet<String> allowedCommands;

    public RANK(String name, ChatColor color, String prefix, int priority) {
        this.name = name;
        this.color = color;
        this.prefix = prefix;
        this.priority = priority;
        this.allowedCommands = new HashSet<>();
    }

    public void addAllowedCommand(String command) {
        allowedCommands.add((command));
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getPriority() {
        return priority;
    }
}
