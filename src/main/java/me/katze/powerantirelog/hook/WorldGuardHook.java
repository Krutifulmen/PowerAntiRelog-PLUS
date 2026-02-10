package me.katze.powerantirelog.hook;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class WorldGuardHook {

    public static final String LEAVE_IN_PVP_MODE_FLAG_NAME = "leave-in-pvpmode";

    private final StateFlag leaveInPvpModeFlag;

    private WorldGuardHook(StateFlag leaveInPvpModeFlag) {
        this.leaveInPvpModeFlag = leaveInPvpModeFlag;
    }

    public static WorldGuardHook createAndRegister(Logger logger) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        StateFlag flag = new StateFlag(LEAVE_IN_PVP_MODE_FLAG_NAME, false);
        try {
            registry.register(flag);
            return new WorldGuardHook(flag);
        } catch (IllegalStateException exception) {
            logger.warning("Cannot register WorldGuard flag '" + LEAVE_IN_PVP_MODE_FLAG_NAME
                    + "' at this stage. Will try to use existing flag only.");
        } catch (FlagConflictException exception) {
            Flag<?> existing = registry.get(LEAVE_IN_PVP_MODE_FLAG_NAME);
            if (existing instanceof StateFlag) {
                return new WorldGuardHook((StateFlag) existing);
            } else {
                throw new IllegalStateException("WorldGuard flag leave-in-pvpmode already exists and is not a StateFlag.");
            }
        }

        return fromExistingFlag(logger);
    }

    public static WorldGuardHook fromExistingFlag(Logger logger) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        Flag<?> existing = registry.get(LEAVE_IN_PVP_MODE_FLAG_NAME);
        if (existing instanceof StateFlag) {
            return new WorldGuardHook((StateFlag) existing);
        }
        if (existing != null) {
            logger.warning("WorldGuard flag '" + LEAVE_IN_PVP_MODE_FLAG_NAME
                    + "' exists but is not StateFlag. WorldGuard integration disabled.");
        }
        return null;
    }

    public ApplicableRegionSet getRegions(org.bukkit.Location bukkitLocation) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        if (container == null) {
            return null;
        }
        RegionQuery query = container.createQuery();
        Location location = new Location(BukkitAdapter.adapt(bukkitLocation.getWorld()), bukkitLocation.getX(),
                bukkitLocation.getY(), bukkitLocation.getZ());
        return query.getApplicableRegions(location);
    }

    public StateFlag.State getFlagState(Player player, org.bukkit.Location bukkitLocation, StateFlag flag) {
        ApplicableRegionSet regions = getRegions(bukkitLocation);
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
