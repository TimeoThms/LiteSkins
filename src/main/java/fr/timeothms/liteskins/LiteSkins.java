package fr.timeothms.liteskins;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class LiteSkins extends JavaPlugin {

    public static LiteSkins instance;
    public static SQLiteManager dbManager;
    public static SkinApplier skinApplier;
    public static FileConfiguration config;
    public static FileConfiguration messages;

    public static String prefix;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        if (!(new File(getDataFolder(), "messages.yml").exists())) {
            this.saveResource("messages.yml", false);
        }
        if (!(new File(getDataFolder(), "skins.db").exists())) {
            this.saveResource("skins.db", false);
        }

        config = this.getConfig();
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
        prefix = Objects.requireNonNull(messages.getString("prefix")).replace("&", "§");

        dbManager = new SQLiteManager();
        skinApplier = new SkinApplier(dbManager);


        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinEventHandler(), this);

        Objects.requireNonNull(getCommand("skin")).setExecutor(new SkinCommand());
        Objects.requireNonNull(getCommand("liteskins")).setExecutor(new LiteSkinsCommand());
    }

    @Override
    public void onDisable() {
        if (dbManager != null) {
            dbManager.close();
        }
    }
    public static LiteSkins getInstance() {
        return instance;
    }
    public static SkinApplier getSkinApplier() {
        return getInstance().skinApplier;
    }
    public static SQLiteManager getSQLiteManager() {
        return dbManager;
    }

    public static String getMessage(String key) {
        String message = Objects.requireNonNull(messages.getString(key)).replace("&", "§");
        return prefix + " " + message;
    }

}
