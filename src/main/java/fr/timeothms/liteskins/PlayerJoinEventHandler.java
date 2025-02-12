package fr.timeothms.liteskins;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static fr.timeothms.liteskins.LiteSkins.config;


public class PlayerJoinEventHandler implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!config.getBoolean("restore-permission") ||
            (config.getBoolean("restore-permission") && player.hasPermission("liteskins.restore"))) {
            LiteSkins.getSkinApplier().applySkin(player, false);
        }
    }
}
