package io.github.superorca.clamp.commands;

import io.github.superorca.clamp.Clamp;
import io.github.superorca.clamp.lib.punishments.Punishment;
import io.github.superorca.clamp.lib.punishments.PunishmentType;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Switch;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.sql.Date;
import java.time.Instant;

@Command("kick")
@CommandPermission("clamp.kick")
public class KickCommand {
    private final Clamp plugin;

    public KickCommand(Clamp plugin) {
        this.plugin = plugin;
    }

    @DefaultFor("kick")
    public void execute(CommandSender sender, OfflinePlayer target, @Optional String reason, @Switch("s") boolean silent) {
        OfflinePlayer mod = (sender instanceof Player) ? (Player) sender : null;

        Punishment punishment = new Punishment(plugin, plugin.generateId(), Date.from(Instant.now()), PunishmentType.KICK, target, mod, reason, null, true);
        punishment.write();

        punishment.display(silent);
        if (target.isOnline()) punishment.execute();
    }
}
