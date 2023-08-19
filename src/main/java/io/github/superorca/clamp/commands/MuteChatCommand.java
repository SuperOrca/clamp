package io.github.superorca.clamp.commands;

import io.github.superorca.clamp.Clamp;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Switch;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static io.github.superorca.clamp.lib.Utils.component;

@Command("mutechat")
@CommandPermission("clamp.mutechat")
public class MuteChatCommand {
    private final Clamp plugin;

    public MuteChatCommand(Clamp plugin) {
        this.plugin = plugin;
    }

    @DefaultFor("mutechat")
    public void execute(CommandSender sender, @Switch("s") boolean silent) {
        String modDisplay = (sender instanceof Player) ? sender.getName() : "Console";

        if (plugin.isChatMute()) {
            plugin.setChatMute(false);
            if (silent)
                Bukkit.broadcast(component("<yellow>[S] <gold>%s <gray>unmuted the chat.".formatted(modDisplay)), "clamp.staff");
            else Bukkit.broadcast(component("<gold>%s <gray>unmuted the chat.".formatted(modDisplay)));
        } else {
            plugin.setChatMute(true);
            if (silent)
                Bukkit.broadcast(component("<yellow>[S] <gold>%s <gray>muted the chat.".formatted(modDisplay)), "clamp.staff");
            else Bukkit.broadcast(component("<gold>%s <gray>muted the chat.".formatted(modDisplay)));
        }
    }
}
