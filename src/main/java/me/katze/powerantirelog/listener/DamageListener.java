package me.katze.powerantirelog.listener;

import me.katze.powerantirelog.manager.PvPManager;
import me.katze.powerantirelog.utility.PlayerUtility;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DamageListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player target = (Player) e.getEntity();
        Player damager = PlayerUtility.getDamager(e.getDamager());

        if (damager == null) return;
        if (damager == target) return;

        PvPManager.addPlayer(target);
        PvPManager.addPlayer(damager);
        PvPManager.recordCombat(damager, target);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCombust(EntityCombustByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player target = (Player) e.getEntity();

        if (!(e.getCombuster() instanceof Player)) return;
        Player damager = (Player) e.getCombuster();

        if (damager == target) return;

        PvPManager.addPlayer(target);
        PvPManager.addPlayer(damager);
        PvPManager.recordCombat(damager, target);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent e) {
        if (e.getPotion() == null) return;
        if (!(e.getPotion().getShooter() instanceof Player)) return;

        Player damager = (Player) e.getPotion().getShooter();

        for (LivingEntity target : e.getAffectedEntities()) {
            if (!(target instanceof Player)) continue;
            if (target == damager) return;

            for (PotionEffect effect : e.getPotion().getEffects()) {
                if (effect.getType().equals(PotionEffectType.POISON)) {

                    PvPManager.addPlayer(damager);
                    PvPManager.addPlayer((Player) target);
                    PvPManager.recordCombat(damager, (Player) target);
                }
            }
        }
    }
}
