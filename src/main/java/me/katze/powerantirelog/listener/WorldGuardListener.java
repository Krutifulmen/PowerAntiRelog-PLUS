package me.katze.powerantirelog.listener;

import me.katze.powerantirelog.AntiRelog;
import me.katze.powerantirelog.hook.WorldGuardHook;
import me.katze.powerantirelog.manager.PvPManager;
import me.katze.powerantirelog.utility.StringUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.codemc.worldguardwrapper.event.WrappedDisallowedPVPEvent;

public class WorldGuardListener implements Listener {

    @EventHandler
    public void onPvP(WrappedDisallowedPVPEvent e) {
        if (!AntiRelog.getInstance().WORLDGUARD_HOOK) {
            return;
        }

        Player attacker = e.getAttacker();
        Player defender = e.getDefender();

        if (PvPManager.isPvP(attacker.getPlayer()) && PvPManager.isPvP(defender.getPlayer())) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!AntiRelog.getInstance().WORLDGUARD_HOOK) {
            return;
        }
        if (event.getTo() == null) {
            return;
        }
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        if (!PvPManager.isPvP(player)) {
            return;
        }
        WorldGuardHook hook = AntiRelog.getInstance().getWorldGuardHook();
        if (hook == null) {
            return;
        }
        boolean fromFlag = hook.isLeaveInPvpMode(player, event.getFrom());
        boolean toFlag = hook.isLeaveInPvpMode(player, event.getTo());
        if (fromFlag && !toFlag) {
            event.setTo(event.getFrom());
            player.sendMessage(StringUtility.getMessage(
                    AntiRelog.getInstance().getConfig().getString("messages.block")));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (!AntiRelog.getInstance().WORLDGUARD_HOOK) {
            return;
        }
        if (event.getTo() == null) {
            return;
        }
        Player player = event.getPlayer();
        if (!PvPManager.isPvP(player)) {
            return;
        }
        WorldGuardHook hook = AntiRelog.getInstance().getWorldGuardHook();
        if (hook == null) {
            return;
        }
        boolean fromFlag = hook.isLeaveInPvpMode(player, event.getFrom());
        boolean toFlag = hook.isLeaveInPvpMode(player, event.getTo());
        if (fromFlag && !toFlag) {
            event.setCancelled(true);
            player.sendMessage(StringUtility.getMessage(
                    AntiRelog.getInstance().getConfig().getString("messages.block")));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEnderPearl(PlayerTeleportEvent event) {
        if (!AntiRelog.getInstance().WORLDGUARD_HOOK) {
            return;
        }
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        Player player = event.getPlayer();
        if (!PvPManager.isPvP(player)) {
            return;
        }
        if (!AntiRelog.getInstance().getConfig().getBoolean("settings.worldguard.cancel.ender-pearl")) {
            return;
        }
        WorldGuardHook hook = AntiRelog.getInstance().getWorldGuardHook();
        if (hook == null || !hook.isLeaveInPvpMode(player, player.getLocation())) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(StringUtility.getMessage(
                AntiRelog.getInstance().getConfig().getString("messages.block")));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGoldenApple(PlayerItemConsumeEvent event) {
        if (!AntiRelog.getInstance().WORLDGUARD_HOOK) {
            return;
        }
        Player player = event.getPlayer();
        if (!PvPManager.isPvP(player)) {
            return;
        }
        if (!AntiRelog.getInstance().getConfig().getBoolean("settings.worldguard.cancel.golden-apple")) {
            return;
        }
        Material type = event.getItem().getType();
        if (type != Material.GOLDEN_APPLE && type != Material.ENCHANTED_GOLDEN_APPLE) {
            return;
        }
        WorldGuardHook hook = AntiRelog.getInstance().getWorldGuardHook();
        if (hook == null || !hook.isLeaveInPvpMode(player, player.getLocation())) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(StringUtility.getMessage(
                AntiRelog.getInstance().getConfig().getString("messages.block")));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPotionThrow(ProjectileLaunchEvent event) {
        if (!AntiRelog.getInstance().WORLDGUARD_HOOK) {
            return;
        }
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
        if (!AntiRelog.getInstance().getConfig().getBoolean("settings.worldguard.cancel.potion-throw")) {
            return;
        }
        WorldGuardHook hook = AntiRelog.getInstance().getWorldGuardHook();
        if (hook == null || !hook.isLeaveInPvpMode(player, player.getLocation())) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(StringUtility.getMessage(
                AntiRelog.getInstance().getConfig().getString("messages.block")));
    }
}
