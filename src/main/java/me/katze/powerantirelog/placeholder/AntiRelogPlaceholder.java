package me.katze.powerantirelog.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.katze.powerantirelog.AntiRelog;
import me.katze.powerantirelog.manager.PvPManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class AntiRelogPlaceholder extends PlaceholderExpansion {
    private final AntiRelog plugin;

    public AntiRelogPlaceholder(AntiRelog plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "powerantirelog";
    }

    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        switch (identifier.toLowerCase()) {
            case "in_pvp":
                return String.valueOf(PvPManager.isPvP(player));
            case "enemy_name":
                return getLastEnemyName(player);
            case "enemy_health":
                return getLastEnemyHealth(player);
            case "enemies":
                return getEnemiesList(player);
            case "time_left":
                return String.valueOf(PvPManager.getTimeLeft(player));
            default:
                return null;
        }
    }

    private String getLastEnemyName(Player player) {
        String enemyName = PvPManager.getLastOpponentName(player);
        if (enemyName == null) {
            return "";
        }
        return enemyName;
    }

    private String getLastEnemyHealth(Player player) {
        String enemyName = PvPManager.getLastOpponentName(player);
        if (enemyName == null) {
            return "";
        }
        Player enemy = Bukkit.getPlayerExact(enemyName);
        if (enemy == null) {
            return "";
        }
        return formatHealth(enemy);
    }

    private String getEnemiesList(Player player) {
        Map<String, Long> enemies = PvPManager.getOpponents(player);
        if (enemies.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String enemyName : enemies.keySet()) {
            Player enemy = Bukkit.getPlayerExact(enemyName);
            if (enemy == null) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(enemy.getName()).append(": ").append(formatHealth(enemy));
        }
        return builder.toString();
    }

    private String formatHealth(Player player) {
        return String.format("%.1f", Math.max(0D, player.getHealth()));
    }
}
