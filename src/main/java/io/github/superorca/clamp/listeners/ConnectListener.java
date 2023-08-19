package io.github.superorca.clamp.listeners;

import io.github.superorca.clamp.Clamp;
import io.github.superorca.clamp.lib.punishments.Punishment;
import io.github.superorca.clamp.lib.punishments.PunishmentType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Optional;

public class ConnectListener implements Listener {
    private final Clamp plugin;

    public ConnectListener(Clamp plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void connect(AsyncPlayerPreLoginEvent e) {
        Optional<Punishment> optional = Punishment.fromTarget(plugin, e.getUniqueId()).stream().filter(punishment -> punishment.getType() == PunishmentType.BAN && punishment.isActive()).findFirst();

        if (optional.isPresent()) {
            Punishment punishment = optional.get();

            if (punishment.getTimespan() != null) {
                long expiration = punishment.getDate().getTime() + punishment.getTimespan().toMillis();
                if (System.currentTimeMillis() >= expiration) {
                    punishment.delete();
                    return;
                }
            }

            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, punishment.getMessage());
        }
    }
}
