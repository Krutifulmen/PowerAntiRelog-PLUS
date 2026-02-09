package me.katze.powerantirelog.hook;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagConflictException;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;

public class WorldGuardHook {

    private final StateFlag leaveInPvpModeFlag;

    public WorldGuardHook() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        StateFlag flag = new StateFlag("leave-in-pvpmode", false);
        try {
            registry.register(flag);
        } catch (FlagConflictException exception) {
            Flag<?> existing = registry.get("leave-in-pvpmode");
            if (existing instanceof StateFlag) {
                flag = (StateFlag) existing;
            } else {
                throw new IllegalStateException("WorldGuard flag leave-in-pvpmode already exists and is not a StateFlag.");
            }
        }
        leaveInPvpModeFlag = flag;
    }

    public ApplicableRegionSet getRegions(Player player, org.bukkit.Location bukkitLocation) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        if (container == null) {
            return null;
        }
        RegionQuery query = container.createQuery();
        Location location = new Location(bukkitLocation.getWorld(), bukkitLocation.getX(),
                bukkitLocation.getY(), bukkitLocation.getZ());
        return query.getApplicableRegions(location);
    }

    public StateFlag.State getFlagState(Player player, org.bukkit.Location bukkitLocation, StateFlag flag) {
        ApplicableRegionSet regions = getRegions(player, bukkitLocation);
        if (regions == null) {
            return null;
        }
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        return regions.queryState(localPlayer, flag);
    }

    public boolean isLeaveInPvpMode(Player player, org.bukkit.Location bukkitLocation) {
        StateFlag.State state = getFlagState(player, bukkitLocation, leaveInPvpModeFlag);
        return state == StateFlag.State.ALLOW;
    }
}
