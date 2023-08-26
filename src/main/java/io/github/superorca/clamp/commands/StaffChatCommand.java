package io.github.superorca.clamp.commands;

import io.github.superorca.clamp.Clamp;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static io.github.superorca.clamp.lib.Utils.component;

@Command({"staffchat", "sc"})
@CommandPermission("clamp.staff")
public class StaffChatCommand {
    private final Clamp plugin;

    public StaffChatCommand(Clamp plugin) {
        this.plugin = plugin;
    }

    @DefaultFor({"staffchat", "sc"})
    public void execute(Player player, @Optional String message) {
        if (message == null) {
            if (plugin.getStaffChat().contains(player.getUniqueId())) {
                plugin.getStaffChat().remove(player.getUniqueId());
                player.sendMessage(component("<yellow>[S] <gray>Staff chat is <red>disabled<gray>."));
            } else {
                plugin.getStaffChat().add(player.getUniqueId());
                player.sendMessage(component("<yellow>[S] <gray>Staff chat is <green>enabled<gray>."));
            }
        } else {
            Bukkit.broadcast(component("<yellow>[S] <gray>%s <dark_gray>⏵ <white>%s".formatted(player.getName(), message)), "clamp.staff");
        }
    }
}