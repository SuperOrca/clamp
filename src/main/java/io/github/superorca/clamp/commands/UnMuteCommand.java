package io.github.superorca.clamp.commands;

import io.github.superorca.clamp.Clamp;
import io.github.superorca.clamp.lib.punishments.Punishment;
import io.github.superorca.clamp.lib.punishments.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Switch;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Objects;

import static io.github.superorca.clamp.lib.Utils.component;

@Command("unmute")
@CommandPermission("clamp.unmute")
public class UnMuteCommand {
    private final Clamp plugin;

    public UnMuteCommand(Clamp plugin) {
        this.plugin = plugin;
    }

    @DefaultFor("unmute")
    public void execute(CommandSender sender, OfflinePlayer target, @Switch("s") boolean silent) {
        OfflinePlayer mod = (sender instanceof Player) ? (Player) sender : null;

        java.util.Optional<Punishment> optional = Punishment.fromTarget(plugin, target.getUniqueId()).stream().filter(punishment -> punishment.getType() == PunishmentType.MUTE && punishment.isActive()).findFirst();

        if (optional.isEmpty()) {
            sender.sendMessage(component("<red>%s is not muted.".formatted(target.getName())));
            return;
        }

        Punishment punishment = optional.get();
        punishment.delete();

        String modDisplay = (mod == null) ? "Console" : sender.getName();

        if (silent) {
            Bukkit.broadcast(component("<yellow>[S] <gold>%s <gray>was unmuted by <gold>%s<gray>.".formatted(target.getName(), modDisplay)), "clamp.staff");
            if (target.isOnline())
                Objects.requireNonNull(target.getPlayer()).sendMessage(component("<gray>You are no longer muted."));
        } else
            Bukkit.broadcast(component("<gold>%s <gray>was unmuted by <gold>%s<gray>.".formatted(target.getName(), modDisplay)));
    }
}
