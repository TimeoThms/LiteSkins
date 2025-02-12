package fr.timeothms.liteskins;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


import java.util.Map;
import java.util.concurrent.Future;

import static fr.timeothms.liteskins.LiteSkins.*;

public class SkinApplier {

    // Instance de SQLiteManager pour les opérations DB

    public SkinApplier(SQLiteManager sqLiteManager) {
    }
    public void applySkin(Player player, boolean sendMessages) {

        // Try to get skin from database
        ProfileProperty dbSkin = LiteSkins.getSQLiteManager().getSkinFromDatabase(player.getUniqueId().toString());
        if (dbSkin != null) {
            Map<String, String> sourceDetails = dbManager.getSourceDetails(player.getUniqueId().toString());
            // If the source is a URL, set the skin to the stored value and signature
            if (sourceDetails.get("source_type").equalsIgnoreCase("url")) {
                Bukkit.getScheduler().runTask(instance, () -> {
                    PlayerProfile profile = player.getPlayerProfile();
                    profile.getProperties().removeIf(property -> property.getName().equals("textures"));
                    profile.getProperties().add(dbSkin);
                    player.setPlayerProfile(profile);
                    if (sendMessages) {
                        player.sendMessage(getMessage("skin-changed"));
                    }
                });
                return;
            }
            else {
                setSkinFromMojang(player, sourceDetails.get("source_value"), false);
                return;
            }

        }
        // If the source type is not a URL, setting/updating skin from MojangAPI
        setSkinFromMojang(player, player.getName(), sendMessages);
        return;
    }

    public void setSkinFromMojang(Player player, String skinUsername, boolean sendMessages) {
        MojangAPI.fetchUUIDFromUsernameAsync(skinUsername, new MojangAPI.Callback<>() {
            @Override
            public void onSuccess(String uuid) {

                MojangAPI.fetchSkinFromUuidAsync(uuid, "value", new MojangAPI.Callback<>() {
                    @Override
                    public void onSuccess(String value) {
                        MojangAPI.fetchSkinFromUuidAsync(uuid, "signature", new MojangAPI.Callback<>() {
                            @Override
                            public void onSuccess(String signature) {
                                if (value == null || signature == null) {
                                    Bukkit.getScheduler().runTask(instance, () -> player.sendMessage(getMessage("skin-change-error")));
                                    return;
                                }

                                ProfileProperty skinProperty = new ProfileProperty("textures", value, signature);
                                Bukkit.getScheduler().runTask(instance, () -> {
                                    PlayerProfile profile = player.getPlayerProfile();
                                    profile.getProperties().removeIf(property -> property.getName().equals("textures"));
                                    profile.getProperties().add(skinProperty);
                                    player.setPlayerProfile(profile);
                                    if (skinUsername.equalsIgnoreCase(player.getName())) {
                                        if (sendMessages) player.sendMessage(getMessage("skin-clear"));
                                    } else {
                                        if (sendMessages) player.sendMessage(getMessage("skin-changed"));
                                    }
                                });
                                dbManager.saveSkinToDatabase(player.getUniqueId().toString(), "premium", skinUsername, value, signature);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                if (sendMessages) Bukkit.getScheduler().runTask(instance, () -> player.sendMessage("Error while fetching skin textures signature"));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (sendMessages) Bukkit.getScheduler().runTask(instance, () -> player.sendMessage("Error while fetching skin textures value"));
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                if (sendMessages) Bukkit.getScheduler().runTask(instance, () -> player.sendMessage(getMessage("unknown-player")));
                dbManager.saveSkinToDatabase(player.getUniqueId().toString(), "default", "none", "", "");
            }
        });
    }

    public void setSkinFromMineskin(Player player, String skinUrl, boolean sendMessages) {
        Future<Map<String, String>> futureSkinData = MineskinClient.getSkinValueAndSignatureAsync(skinUrl);

        Bukkit.getScheduler().runTask(instance, () -> {
            try {
                Map<String, String> skinData = futureSkinData.get(); // Bloque jusqu'à ce que les données soient disponibles
                String value = skinData.get("value");
                String signature = skinData.get("signature");

                if (value != null && signature != null) {

                    ProfileProperty skinProperty = new ProfileProperty("textures", value, signature);
                    Bukkit.getScheduler().runTask(instance, () -> {
                        PlayerProfile profile = player.getPlayerProfile();
                        profile.getProperties().removeIf(property -> property.getName().equals("textures"));
                        profile.getProperties().add(skinProperty);
                        player.setPlayerProfile(profile);
                        if (sendMessages) player.sendMessage(getMessage("skin-changed"));

                    });
                    dbManager.saveSkinToDatabase(player.getUniqueId().toString(), "url", skinUrl, value, signature);

                } else {
                    if (sendMessages) {
                        player.sendMessage(getMessage("invalid-url"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage("§cAn error occurred while applying the skin.");
            }
        });
    }
}

