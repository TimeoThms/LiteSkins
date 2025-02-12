package fr.timeothms.liteskins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

import static fr.timeothms.liteskins.LiteSkins.*;


public class SkinCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1 && !(sender instanceof Player)) {
                instance.getLogger().info("This command is only usable by players. Please use /skin <clear/username/URL> <player>");
                return false;
            }
            if (args.length != 1) {
                if (!sender.hasPermission("liteskins.skin.other")) {
                    sender.sendMessage(getMessage("skin-command-usage"));
                    return false;
                } else if (args.length < 1 || args.length > 2) {
                    sender.sendMessage(getMessage("skin-command-usage-other"));
                    return false;
                }
            }

            String arg = args[0];
            String targetUsername;
            Player target;
            if (args.length == 2) {
                targetUsername = args[1];
                target = Bukkit.getPlayer(targetUsername);
                if (target == null) {
                    sender.sendMessage(getMessage("player-not-found"));
                    return false;
                }
            } else {
                target = (Player) sender;
            }

            if (arg.equalsIgnoreCase("clear")) {
                if (sender.hasPermission("liteskins.skin.username") || sender.hasPermission("liteskins.skin.url")) {
                    skinApplier.setSkinFromMojang(target, target.getName(), true);
                    return true;
                } else {
                    sender.sendMessage(getMessage("permission-skin"));
                    return false;
                }
            } else if (isValidURL(arg) && config.getBoolean("mineskin.enabled")) {
                if (sender.hasPermission("liteskins.skin.url")) {
                    skinApplier.setSkinFromMineskin(target, arg, true);
                } else {
                    sender.sendMessage(getMessage("permission-url"));
                    return false;
                }
            } else {
                if (sender.hasPermission("liteskins.skin.username")) {
                    if ((config.getBoolean("blacklist.enabled") &&config.getStringList("blacklist.blacklisted-skins").contains(arg)) &&
                            !sender.hasPermission("liteskins.bypassblacklist")) {
                        sender.sendMessage(getMessage("blacklisted-skin"));
                        return false;
                    } else {
                        skinApplier.setSkinFromMojang(target, arg, true);
                    }
                } else {
                    sender.sendMessage(getMessage("permission-username"));
                    return false;
                }
            }
            if (args.length == 2) {
                sender.sendMessage(getMessage("skin-changed-other").replace("%target%", target.getName()));
            }
            return true;
        }

    public static boolean isValidURL(String urlString) {
        try {
            URI uri = new URI(urlString);
            return uri.isAbsolute() && (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https"));
        } catch (URISyntaxException e) {
            return false;
        }
    }

}
