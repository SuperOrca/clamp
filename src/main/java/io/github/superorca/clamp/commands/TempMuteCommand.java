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
import java.time.Duration;
import java.time.Instant;

@Command("tempmute")
@CommandPermission("clamp.tempmute")
public class TempMuteCommand {
    private final Clamp plugin;

    public TempMuteCommand(Clamp plugin) {
        this.plugin = plugin;
    }

    @DefaultFor("tempmute")
    public void execute(CommandSender sender, OfflinePlayer target, Duration timespan, @Optional String reason, @Switch("s") boolean silent) {
        OfflinePlayer mod = (sender instanceof Player) ? (Player) sender : null;

        Punishment punishment = new Punishment(plugin, plugin.generateId(), Date.from(Instant.now()), PunishmentType.MUTE, target, mod, reason, timespan, true);
        punishment.write();

        punishment.display(silent);
    }
}
