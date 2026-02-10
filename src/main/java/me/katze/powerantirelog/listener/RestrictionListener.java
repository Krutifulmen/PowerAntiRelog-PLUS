package me.katze.powerantirelog.listener;

import me.katze.powerantirelog.AntiRelog;
import me.katze.powerantirelog.manager.PvPManager;
import me.katze.powerantirelog.utility.StringUtility;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class RestrictionListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!PvPManager.isPvP(player)) {
            return;
        }
        if (!AntiRelog.getInstance().getConfig().getBoolean("settings.cancel.block-place")) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(StringUtility.getMessage(
                AntiRelog.getInstance().getConfig().getString("messages.block")));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!PvPManager.isPvP(player)) {
            return;
        }
        if (!AntiRelog.getInstance().getConfig().getBoolean("settings.cancel.block-break")) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(StringUtility.getMessage(
                AntiRelog.getInstance().getConfig().getString("messages.block")));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPotionThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof ThrownPotion)) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        if (!PvPManager.isPvP(player)) {
            return;
        }
        if (!AntiRelog.getInstance().getConfig().getBoolean("settings.cancel.potion-throw")) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(StringUtility.getMessage(
                AntiRelog.getInstance().getConfig().getString("messages.block")));
    }
}
