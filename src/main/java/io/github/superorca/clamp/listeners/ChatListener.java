package io.github.superorca.clamp.listeners;

import io.github.superorca.clamp.Clamp;
import io.github.superorca.clamp.lib.punishments.Punishment;
import io.github.superorca.clamp.lib.punishments.PunishmentType;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;

import static io.github.superorca.clamp.lib.Utils.component;

public class ChatListener implements Listener {
    private final Clamp plugin;

    public ChatListener(Clamp plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void chat(AsyncChatEvent e) {
        Player player = e.getPlayer();

        Optional<Punishment> optional = Punishment.fromTarget(plugin, player.getUniqueId()).stream().filter(punishment -> punishment.getType() == PunishmentType.MUTE && punishment.isActive()).findFirst();

        if (optional.isPresent()) {
            Punishment punishment = optional.get();

            if (punishment.getTimespan() != null) {
                long expiration = punishment.getDate().getTime() + punishment.getTimespan().toMillis();
                if (System.currentTimeMillis() >= expiration) {
                    punishment.delete();
                    return;
                }
            }

            e.setCancelled(true);
            player.sendMessage(punishment.getMessage());
            return;
        }

        if (plugin.getStaffChat().contains(player.getUniqueId())) {
            if (!player.hasPermission("clamp.staff")) {
                plugin.getStaffChat().remove(player.getUniqueId());
            } else {
                e.setCancelled(true);
                Bukkit.broadcast(component("<yellow>[S] <gray>%s <dark_gray>⏵ <white>%s".formatted(player.getName(), e.message())), "clamp.staff");
                return;
            }
        }

        if (plugin.isChatMute() && !player.hasPermission("clamp.staff")) {
            e.setCancelled(true);
            player.sendMessage(component("<red>Cannot chat while chat is muted."));
        }
    }
}
