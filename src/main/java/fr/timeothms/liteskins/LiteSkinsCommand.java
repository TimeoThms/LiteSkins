package fr.timeothms.liteskins;

import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
import java.util.stream.Collectors;

import static fr.timeothms.liteskins.LiteSkins.*;

public class LiteSkinsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            PluginMeta pm = instance.getPluginMeta();
            sender.sendMessage("§7This server is using " + pm.getName() + " " + pm.getVersion() + " by " +
                    pm.getAuthors().stream().collect(Collectors.joining(", ")) + ". Download it at " +
                    pm.getWebsite());
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("liteskins.reload")) {
                    instance.reloadConfig();
                    config = instance.getConfig();

                    messages = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "messages.yml"));
                    prefix = Objects.requireNonNull(messages.getString("prefix")).replace("&", "§");

                    sender.sendMessage(getMessage("reload"));
                    return true;
                }
            }
        }
        return false;
    }
}
