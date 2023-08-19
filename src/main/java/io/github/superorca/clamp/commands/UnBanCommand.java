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

import java.util.Optional;

import static io.github.superorca.clamp.lib.Utils.component;

@Command("unban")
@CommandPermission("clamp.unban")
public class UnBanCommand {
    private final Clamp plugin;

    public UnBanCommand(Clamp plugin) {
        this.plugin = plugin;
    }

    @DefaultFor("unban")
    public void execute(CommandSender sender, OfflinePlayer target, @Switch("s") boolean silent) {
        OfflinePlayer mod = (sender instanceof Player) ? (Player) sender : null;

        Optional<Punishment> optional = Punishment.fromTarget(plugin, target.getUniqueId()).stream().filter(punishment -> punishment.getType() == PunishmentType.BAN && punishment.isActive()).findFirst();

        if (optional.isEmpty()) {
            sender.sendMessage(component("<red>%s is not banned.".formatted(target.getName())));
            return;
        }

        Punishment punishment = optional.get();
        punishment.delete();

        String modDisplay = (mod == null) ? "Console" : sender.getName();

        if (silent)
            Bukkit.broadcast(component("<yellow>[S] <gold>%s <gray>was unbanned by <gold>%s<gray>.".formatted(target.getName(), modDisplay)), "clamp.staff");
        else
            Bukkit.broadcast(component("<gold>%s <gray>was unbanned by <gold>%s<gray>.".formatted(target.getName(), modDisplay)));
    }
}
