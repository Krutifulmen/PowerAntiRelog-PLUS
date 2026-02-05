package me.katze.powerantirelog;

import co.aikar.commands.PaperCommandManager;
import me.katze.powerantirelog.command.HelpCommand;
import me.katze.powerantirelog.command.ReloadCommand;
import me.katze.powerantirelog.command.TestCommand;
import me.katze.powerantirelog.listener.*;
import me.katze.powerantirelog.manager.CooldownManager;
import me.katze.powerantirelog.manager.PvPManager;
import me.katze.powerantirelog.placeholder.AntiRelogPlaceholder;
import me.katze.powerantirelog.utility.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiRelog extends JavaPlugin {

    public boolean CMI_HOOK = false;
    public boolean ESSENTIALS_HOOK = false;
    public boolean WORLDGUARD_HOOK = false;
    public boolean PLACEHOLDERAPI_HOOK = false;

    private final int PLUGIN_ID = 23642;
    public final String VERSION = "1.6";
    public final String CREATOR = "https://github.com/katze225/PowerAntiRelog";
    public final String TELEGRAM_URL = "https://t.me/core2k21";

    private static AntiRelog instance;
    public PvPManager pvpmanager;
    public CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;

        System.out.println("Version: " + CREATOR);
        System.out.println("By: " + VERSION);
        System.out.println("Support: " + TELEGRAM_URL);

        loadDepend();
        loadMetrics();
        loadConfig();

        loadListeners();
        loadCommands();
        loadPlaceholders();

        pvpmanager = new PvPManager();
        cooldownManager = new CooldownManager();
    }

    @Override
    public void onDisable() {
    }

    private void loadCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(AntiRelog.getInstance());

        commandManager.registerCommand(new HelpCommand());
        commandManager.registerCommand(new ReloadCommand());
        commandManager.registerCommand(new TestCommand());
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new WorldGuardListener(), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new LeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new CommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new CooldownListener(), this);
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void loadPlaceholders() {
        if (PLACEHOLDERAPI_HOOK) {
            new AntiRelogPlaceholder(this).register();
        }
    }

    public void loadMetrics() {
        Metrics metrics = new Metrics(this, PLUGIN_ID);
    }

    public void loadDepend() {
        if (getServer().getPluginManager().getPlugin("CMI") != null) {
            CMI_HOOK = true;
        }
        if (getServer().getPluginManager().getPlugin("Essentials") != null) {
            ESSENTIALS_HOOK = true;
        }
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            WORLDGUARD_HOOK = true;
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PLACEHOLDERAPI_HOOK = true;
        }
    }

    public static AntiRelog getInstance() {
        return instance;
    }
}
